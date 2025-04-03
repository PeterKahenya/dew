from pkgutil import get_data
from typing import Any, Sequence
import uuid
from datetime import datetime, timezone, timedelta
from fastapi import Cookie, Depends, FastAPI, HTTPException, Header, Query, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.security import OAuth2PasswordBearer
from fastapi.security.utils import get_authorization_scheme_param
from httpx import request
import jwt
from passlib.context import CryptContext
from sqlalchemy import Boolean, ForeignKey, String, create_engine, delete, exc, or_, select, update
from sqlalchemy.orm import DeclarativeBase, Mapped, Session, mapped_column, relationship, sessionmaker
from pydantic_settings import BaseSettings
from pydantic import UUID4, BaseModel
from llama import Dialog, Llama
import os
import torch
import pymysql

# Models
class Model(DeclarativeBase):
    def to_dict(self):
        object_dict = {c.name: getattr(self, c.name) for c in self.__table__.columns}
        for key, value in object_dict.items():
            if isinstance(value, datetime):
                object_dict[key] = value.isoformat()
            elif isinstance(value, uuid.UUID):
                object_dict[key] = str(value)
        return object_dict
    
class User(Model):
    __tablename__ = "users"
    id: Mapped[uuid.UUID] = mapped_column(primary_key=True,unique=True,default=uuid.uuid4)    
    name: Mapped[str] = mapped_column(String(100),unique=False)
    email: Mapped[str] = mapped_column(String(100),unique=True)
    password: Mapped[str] = mapped_column(String(500),unique=False)
    created_at: Mapped[datetime] = mapped_column(default=datetime.now)
    updated_at: Mapped[datetime | None] = mapped_column(onupdate=datetime.now)
    tasks: Mapped[list["Task"]] = relationship("Task",back_populates="user")

    def create_jwt_token(self, secret: str, algorithm: str, expiry_seconds: int) -> str:
        """
        Create a JWT token for the user, encoding the email and expiry time and return it
        """
        print(f"Creating JWT token for user {self.name}")
        expire = datetime.now(timezone.utc) + timedelta(seconds=expiry_seconds)
        payload = {
            "sub": self.email,
            "exp": expire
        }
        return jwt.encode(payload=payload,key=secret,algorithm=algorithm)
    
    @staticmethod
    def verify_jwt_token(db: Session, token: str, secret: str, algorithm) -> tuple[str,str,str] | None:
        """
            Verify the JWT token and return the email
        """
        print(f"Verifying JWT token {token}")
        try:
            payload = jwt.decode(jwt=token,key=secret,algorithms=[algorithm],options={"verify_exp":True,"verify_signature":True,"required":["exp","sub"]})
            return payload.get("sub",None)
        except jwt.InvalidAlgorithmError:
            print(f"JWT token invalid algorithm: {algorithm} on token: {token}")
            raise HTTPException(status_code=401,detail={"message":"Invalid access token"})
        except jwt.ExpiredSignatureError:
            print(f"JWT expired signature on token: {token}")
            raise HTTPException(status_code=401,detail={"message":"Access Token expired"})
        except jwt.InvalidTokenError as e:
            print(f"JWT invalid token: {token} error: {e}")
            raise HTTPException(status_code=401,detail={"message":"Invalid access token"})
    
    def set_password(self, password):
        pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
        self.password = pwd_context.hash(password)
        return self

    def check_password(self, password):
        pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
        return pwd_context.verify(password, self.password)

class Task(Model):
    __tablename__ = "tasks"
    id: Mapped[uuid.UUID] = mapped_column(primary_key=True,unique=True,default=uuid.uuid4)    
    title: Mapped[str] = mapped_column(String(100))
    description: Mapped[str] = mapped_column(String(2000))
    is_complete: Mapped[bool] = mapped_column(Boolean, default=False)
    completed_at: Mapped[datetime | None] = mapped_column()
    created_at: Mapped[datetime] = mapped_column(default=datetime.now)
    updated_at: Mapped[datetime | None] = mapped_column(onupdate=datetime.now)
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.id",ondelete="CASCADE"))
    user: Mapped[User] = relationship("User", back_populates="tasks")

# Schemas
class ListResponse(BaseModel):
    total: int
    page: int
    size: int
    data: list[object]

class ModelInDBBase(BaseModel):
    id: UUID4
    created_at: datetime
    updated_at: datetime | None

    model_config = {
        "from_attributes": True
    }

class UserLogin(BaseModel):
    email: str
    password: str 
    
class UserCreate(BaseModel):
    name: str
    email: str
    password: str
    confirm_password: str

class UserUpdate(BaseModel):
    name: str | None = None
    email: str | None = None
    password: str | None = None
    confirm_password: str | None = None

class UserInDB(ModelInDBBase):
    name: str
    email: str

class TaskCreate(BaseModel):
    title: str
    description: str
    is_complete: bool | None  = False
    completed_at: datetime | None = None

class TaskUpdate(BaseModel):
    title: str | None = None
    description: str | None = None
    is_complete: bool | None = None
    completed_at: datetime | None = None

class TaskInDB(ModelInDBBase):
    title: str 
    description: str 
    is_complete: bool 
    completed_at: datetime | None
    user: UserInDB

class ChatDialogMessage(BaseModel):
    role: str
    content: str

# CRUD Operations
async def create_user(db: Session, user_create: UserCreate) -> Model:
    print(f"Creating User with params: {user_create.model_dump()}")
    user = User(name=user_create.name, email=user_create.email)
    user = user.set_password(user_create.password)
    db.add(user)
    db.commit()
    db.refresh(user)
    return user

async def create_task(db: Session, task_create: TaskCreate, user: User) -> Task:
    print(f"Creating Task with params: {task_create.model_dump()}")
    obj = Task(**task_create.model_dump(exclude_none=True))
    obj.user_id = user.id
    db.add(obj)
    db.commit()
    db.refresh(obj)
    return obj

async def get_obj_or_404(db: Session, model: Model, id: UUID4) -> Model:
    """
        Returns one object from the database by pk id or raise an exception:  sqlalchemy.orm.exc.NoResultFound if no result is found
    """
    print(f"Getting {model.__name__} with id: {id}")
    try:
        return db.execute(select(model).where(model.id == id)).scalar_one()
    except exc.NoResultFound:
        raise HTTPException(status_code=404,detail={"message":f"{model.__name__} with id {id} not found"})

async def filter_objects(db: Session, model: Model, params: dict = {}, sort_by:str = "created_at,asc" ) -> Sequence[Model]:
    """
        Returns a list of objects from the database filtered by the given params
    """
    print(f"Filtering {model.__tablename__} with params: {params}")
    try:
        sort_key, sort_order = tuple(sort_by.split(','))
        sort_column = getattr(model, sort_key)
        CONDITION_MAP = {
            'eq': lambda col, val: col == val,
            'ne': lambda col, val: col != val,
            'lt': lambda col, val: col < val,
            'lte': lambda col, val: col <= val,
            'gt': lambda col, val: col > val,
            'gte': lambda col, val: col >= val,
            'like': lambda col, val: col.like(val),
            'ilike': lambda col, val: col.ilike("%"+val+"%"),
            'contains': lambda col, val: col.contains(val),
            'in': lambda col, val: col.in_(val),
        }
        query = select(model)
        for key, value in params.items():
            if '__' in key:
                column_name, condition = key.split('__', 1)
            else:
                column_name, condition = key, 'eq'
            column = getattr(model, column_name)
            if isinstance(column.type, Boolean):
                if isinstance(value, str):
                    value = value.lower() in ('true','True', 't', 'yes', 'y', '1')
            query = query.where(CONDITION_MAP[condition](column, value))
        query = query.order_by(sort_column.asc() if sort_order == 'asc' else sort_column.desc())
        print(f"Query: {query.compile()}")
        return db.scalars(query).all()
    except Exception as e:
        print(f"Error: {str(e)} filtering {model.__tablename__} with params: {params}")
        raise e
    
async def search_objects(db: Session, model: Model, q: str) -> Sequence[Model]:
    print(f"Searching {model.__tablename__} with query: {q}")
    query = select(model)
    conditions = []
    for column in model.__table__.columns:
        if column.type.python_type == str:
            conditions.append(column.ilike(f"%{q}%"))
    if conditions:
        query = query.where(or_(*conditions))
    return db.scalars(query).all()

async def update_obj(db: Session, model: Model, obj_id: UUID4, obj_update: UserUpdate|TaskUpdate) -> Model:
    print(f"Updating {model.__tablename__} with id: {obj_id}")
    obj = await get_obj_or_404(db, model, obj_id)
    obj_update_cleaned = obj_update.model_dump(exclude_unset=True, exclude_none=True)
    db.execute(update(model).where(model.id == obj_id).values(**obj_update_cleaned))
    db.commit()
    db.refresh(obj)
    return obj

async def delete_obj(db: Session, model: Model, id: UUID4) -> bool:
    print(f"Deleting {model.__name__} with id: {id}")
    obj = await get_obj_or_404(db, model, id)
    db.execute(delete(model).where(model.id == obj.id))
    db.commit()
    return True

# Settings
class AppSettings(BaseSettings):
    mysql_driver: str = "mysql+pymysql"
    mysql_host: str
    mysql_port: int
    mysql_user: str
    mysql_password: str
    mysql_database: str
    secret: str

settings = AppSettings()

# Initializations
DB_URL = f"{settings.mysql_driver}://{settings.mysql_user}:{settings.mysql_password}@{settings.mysql_host}:{settings.mysql_port}/{settings.mysql_database}"
engine = create_engine(DB_URL,pool_pre_ping=True, pool_size=10, max_overflow=20,pool_recycle=3600)
Model.metadata.create_all(bind=engine)
session_local = sessionmaker(autocommit=False,autoflush=False,bind=engine)

def get_db():
    db = None
    try:
        db = session_local()
        print("Database connection established")
        yield db
    except pymysql.MySQLError as e:
        print(f"Database connection error: {e}")
        if db:
            db.close()
        raise
    finally:
        if db:
            print("Closing database connection")
            db.close()

def initialize_llama(rank: int, world_size: int, ckpt_dir, tokenizer_path, max_seq_len, max_batch_size):
    try:
        os.environ['RANK'] = str(rank)
        os.environ['WORLD_SIZE'] = str(world_size)
        os.environ['LOCAL_RANK'] = str(rank)
        os.environ['MASTER_ADDR'] = 'localhost'
        os.environ['MASTER_PORT'] = '29511'
        llama = Llama.build(
            ckpt_dir=ckpt_dir,
            tokenizer_path=tokenizer_path,
            max_seq_len=max_seq_len,
            max_batch_size=max_batch_size,
        )
        return llama
    finally:
        torch.distributed.destroy_process_group()

llama = initialize_llama(
        rank=0,
        world_size=1,
        ckpt_dir="llama/Llama3.2-1B-Instruct",
        tokenizer_path="llama/Llama3.2-1B-Instruct/tokenizer.model",
        max_seq_len=6120,
        max_batch_size=1,
    )

def prompt_llama(context_tasks: list[dict], prompt: str) -> str:
    datetime_now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    system_prompt = f"""
    You are my assistant for a task management app called Dew. 
    Below are my most recent tasks in JSON format: 

    {str(context_tasks)}

    Please respond in a format that's easy to read without mentioning the JSON or the IDs(unless absolutely necessary). 
    The date and time right now is: {datetime_now}. 
    """
    dialogs: list[Dialog] = [
        [
            {
                "role": "system",
                "content":system_prompt
            },
            {
                "role": "user",
                "content": f"{prompt[:3000]}"
            }
        ]
    ]
    results = llama.chat_completion(
        dialogs,
        max_gen_len=2048,
        temperature=0.6,
        top_p=0.9,
    )[0] # dealing with only one batch
    return results["generation"]["content"]


# APIs
app = FastAPI(**{
    "title":"Dew Service",
    "debug":True, # Read this from an Environment variable
    "root_path": "/api",
})
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def welcome():
    return {"message":"Welcome to dew"}

@app.post("/signup", response_model=UserInDB, status_code=201)
async def signup(
    user_create: UserCreate,
    db: Session = Depends(get_db)
):
    # TODO: Test password quality
    # TODO: Handle pymysql.err.IntegrityError
    if user_create.password != user_create.confirm_password:
        raise HTTPException(status_code=400,detail={
            "message":"Passwords do not match"
        })
    user_create.confirm_password = None
    user = await create_user(db, user_create)
    return user

@app.post("/login", status_code=200)
async def login(
    user_login: UserLogin,
    db: Session = Depends(get_db)
):
    user = db.execute(select(User).where(User.email == user_login.email)).scalar_one_or_none()
    if user and user.check_password(user_login.password):
        access_token = user.create_jwt_token(settings.secret, algorithm="HS256", expiry_seconds=7200)
        response = JSONResponse(content={"message": "Logged In","access_token":access_token,"expiry_seconds":7200})
        response.set_cookie(key="access_token", value=access_token)
        return response
    else:
        raise HTTPException(status_code=401,detail={
            "message":"Invalid email or password"
        })

def current_logged_in_user(request:Request, db: Session = Depends(get_db)):
    scheme,param = get_authorization_scheme_param(request.headers.get("Authorization",None))
    access_token = param if scheme.lower() == "bearer" else None
    cookie_token = request.cookies.get("access_token")
    if cookie_token:
        access_token = cookie_token  # Use the token from the cookie
    if not access_token:
        raise HTTPException(status_code=401, detail="Not authenticated")
    email = User.verify_jwt_token(db, access_token, secret=settings.secret, algorithm="HS256")
    user = db.execute(select(User).where(User.email == email)).scalar_one_or_none()
    if not user:
        raise HTTPException(status_code=401,detail={
            "message":"Invalid Access Token"
        })
    return user

@app.post("/logout", status_code=200)
async def login(
    user: User = Depends(current_logged_in_user),
    db: Session = Depends(get_db),
):
    user = db.execute(select(User).where(User.email == user.email)).scalar_one_or_none()
    if user:
        response = JSONResponse(content={"message": "Logged Out"})
        response.set_cookie(
            key="access_token",
            value=None,
            httponly=True,
            secure=True,
            samesite="Lax",
            path="/",
            expires=0
        )
        return response
    else:
        raise HTTPException(status_code=401,detail={
            "message":"Invalid Session. Please login"
        })
    
@app.get("/me", response_model=UserInDB, status_code=200)
async def profile(
    user: User = Depends(current_logged_in_user)
):
    return user

@app.post("/users/{user_id}/tasks", response_model=TaskInDB, status_code=201)
async def add_task(
    user_id: UUID4,
    task_create: TaskCreate,
    _: User = Depends(current_logged_in_user),
    db: Session = Depends(get_db),
):
    user = await get_obj_or_404(db, User, user_id)
    task = await create_task(db, task_create, user)
    return task

async def get_query_params(
    request: Request,
    page: int = Query(1, ge=1),
    size: int = Query(10, ge=1, le=100),
    q: str | None = None,
) -> dict[str, Any]:
    query_params = dict(request.query_params)
    query_params.pop('page', None)
    query_params.pop('size', None)
    query_params.pop('q', None)
    params = {
        "page": page,
        "size": size,
        "q": q,
        **query_params
    }
    return params

async def paginate(
                    db: Session, 
                    model: Model,
                    schema: BaseModel,
                    q: str | None = None,
                    page: int = Query(1, ge=1),
                    size: int = Query(10, ge=1, le=100),
                    sort_by: str = "created_at,asc",
                    **params
                ) -> ListResponse:
    if q:
        data = await search_objects(db=db, model=model,q=q)
    elif params and len(params) > 0:
        data = await filter_objects(db=db, model=model,params=params,sort_by=sort_by)
    else:
        data = await filter_objects(db=db, model=model, params={},sort_by=sort_by)
    offset = (page - 1) * size
    total = len(data)
    paginated_items = data[offset:offset + size]
    paginated_items = [schema.model_validate(item) for item in paginated_items]
    return ListResponse(**{
        "total": total,
        "page": page,
        "size": size,
        "data": paginated_items
    })

@app.get("/users/{user_id}/tasks", response_model=ListResponse, status_code=200)
async def get_tasks(
    user_id: UUID4,
    params: dict[str, Any] = Depends(get_query_params),
    _: User = Depends(current_logged_in_user),
    db: Session = Depends(get_db)
):
    user = await get_obj_or_404(db, User, user_id)
    params["user_id"] = user_id
    return await paginate(db,Task,TaskInDB,**params)

@app.put("/users/{user_id}/tasks/{task_id}", response_model=TaskInDB, status_code=200)
async def update_task(
    user_id: UUID4,
    task_id: UUID4,
    update_task: TaskUpdate,
    _: User = Depends(current_logged_in_user),
    db: Session = Depends(get_db)
):
    user = await get_obj_or_404(db, User, user_id)
    task_db = await get_obj_or_404(db, Task, task_id)
    if task_db.user.id != user.id:
        raise HTTPException(status_code=403,detail={
            "message":"You do not have permission to modify this task"
        })
    task_db: Task = await update_obj(db, Task, task_id, update_task)
    if update_task.is_complete and not update_task.completed_at:
        task_db.completed_at = datetime.now(tz=timezone.utc)
    db.commit()
    db.refresh(task_db)
    return task_db

@app.delete("/users/{user_id}/tasks/{task_id}/", response_model=None, status_code=204)
async def update_task(
    user_id: UUID4,
    task_id: UUID4,
    _: User = Depends(current_logged_in_user),
    db: Session = Depends(get_db)
):
    user = await get_obj_or_404(db, User, user_id)
    task_db = await get_obj_or_404(db, Task, task_id)
    if task_db.user.id != user.id:
        raise HTTPException(status_code=403,detail={
            "message":"You do not have permission to modify this task"
        })
    is_deleted = await delete_obj(db, Task, task_id)
    if is_deleted:
        return None

# POST /api/users/{user_id}/chat to chat with llama that will have the context of this user's tasks
@app.post("/users/{user_id}/chat", response_model=ChatDialogMessage,status_code=200)
async def chat(
    user_id: UUID4,
    prompt: ChatDialogMessage,
    _: User = Depends(current_logged_in_user),
    db: Session = Depends(get_db)
):
    try:
        user = await get_obj_or_404(db, User, user_id)
        tasks = db.execute(select(Task).where(Task.user_id == user.id).order_by(Task.created_at.desc())).scalars()
        context_tasks = [task.to_dict() for task in tasks]
        print(f"Sending {len(context_tasks)} tasks to the LLM")
        llama_response = prompt_llama(context_tasks, prompt.content)
        return ChatDialogMessage(role="assistant",content=llama_response)
    except Exception as e:
        raise HTTPException(status_code=500,detail=f"{str(e)}")