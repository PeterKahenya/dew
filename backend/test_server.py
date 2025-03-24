import uuid
from fastapi import HTTPException
from fastapi.testclient import TestClient
from pydantic_settings import BaseSettings
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from server import (
    User, Task, Model, 
    ListResponse, UserCreate, UserUpdate, UserInDB, TaskCreate, TaskUpdate, TaskInDB,
    create_user, create_task, delete_obj, update_obj, delete_obj, get_obj_or_404, filter_objects, search_objects, app, get_db)
from sqlalchemy.sql import select
from datetime import datetime, timezone

class AppSettingsforTest(BaseSettings):
    mysql_driver: str = "mysql+pymysql"
    mysql_host: str
    mysql_port: int
    mysql_user: str
    mysql_password: str

settings = AppSettingsforTest()

# Initializations
TEST_DB_URL = f"{settings.mysql_driver}://{settings.mysql_user}:{settings.mysql_password}@{settings.mysql_host}:{settings.mysql_port}/dew_test"
engine = create_engine(TEST_DB_URL)
Model.metadata.create_all(bind=engine)
SessionLocal = sessionmaker(autocommit=False,autoflush=False,bind=engine)

@pytest.fixture(scope="session")
def db():
    session = SessionLocal(bind=engine)
    yield session
    session.rollback()
    session.close()
    Model.metadata.drop_all(bind=engine)
    engine.dispose()

pytest_plugins = ('pytest_asyncio',)

# Test Models
def test_to_dict_method(db):
    user = User(name="TestUser1",email="test.user1@example.com",password="ssddsdsdsddsfdf")
    db.add(user)
    db.commit()
    db.refresh(user)
    user_dict = user.to_dict()
    assert user_dict["id"] == str(user.id)
    assert user_dict["name"] == "TestUser1"
    assert user_dict["email"] == "test.user1@example.com"
    assert user_dict["created_at"] == user.created_at.isoformat()
    assert user_dict["updated_at"] == None

def test_user_model(db):
    user = User(name="TestUser2",email="test.user2@example.com",password="ssddsdsdsddsfdf")
    db.add(user)
    db.commit()
    db.refresh(user)
    assert user in db

def test_task_model(db):
    user = db.execute(select(User).where(User.email == "test.user1@example.com")).scalars().first()
    task = Task(title="test task 1", description="test task 1 descr", is_complete=True, completed_at=datetime.now(tz=timezone.utc), user_id=user.id)
    db.add(task)
    db.commit()
    db.refresh(task)
    assert task in db

# Test Schemas
def test_common_schema(db):
    users = db.execute(select(User)).scalars().all()
    list_response = ListResponse(**{
        "total": len(users),
        "page": 1,
        "size": len(users),
        "data": [UserInDB.model_validate(user).model_dump() for user in users]
    })
    list_response_json = list_response.model_dump()
    assert list_response_json["total"] == len(users)
    assert list_response_json["page"] == 1
    assert list_response_json["size"] == len(users)

def test_user_schema(db):
    user_create = UserCreate(**{"name": "TestUser3","email":"testuser3@example.com","password":"pass","confirm_password":"pass"})
    assert user_create.name == "TestUser3"
    user_update = UserUpdate(**{"name": "TestUser4"})
    assert user_update.name == "TestUser4"
    user_db = db.execute(select(User)).scalars().first()
    user_schema = UserInDB.model_validate(user_db)
    assert user_schema.name == user_db.name

def test_task_schema(db):
    task_create = TaskCreate(**{"title": "Test Task 2","description":"Test Task 2 descr","is_complete":True,"completed_at":datetime.now().isoformat()})
    assert task_create.title == "Test Task 2"
    task_update = TaskUpdate(**{"title": "Test Task 3"})
    assert task_update.title == "Test Task 3"
    task_db = db.execute(select(Task)).scalars().first()
    task_schema = TaskInDB.model_validate(task_db)
    assert task_schema.title == task_db.title
    
# Test CRUD
@pytest.mark.asyncio
async def test_get_obj_or_404(db):
    user_db = db.execute(select(User)).scalars().first()
    user_db = await get_obj_or_404(db, User, user_db.id)
    assert user_db is not None
    assert user_db in db
    with pytest.raises(HTTPException) as e:
        await get_obj_or_404(db, User, uuid.uuid4())
    
@pytest.mark.asyncio
async def test_filter_objects(db):
    params = {
        "name__ilike": "%U%", 
        "email__ilike": "%@%",
        "created_at__gte": "2021-01-01",
    }
    users = await filter_objects(db, User, params,sort_by="name,asc")
    assert users is not None
    assert all([user.name.lower().find("u") > -1 for user in users])
    users = await filter_objects(db, User, {"name__contains":"ricardo shilly shally"})
    assert users is not None
    assert len(users) == 0
    with pytest.raises(AttributeError):
        await filter_objects(db, User, {"country__eq":"Narnia"})

@pytest.mark.asyncio
async def test_search_objects(db):
    users = await search_objects(db, User, "test")
    assert users is not None
    assert all([user.name.lower().find("test") > -1 for user in users])
    users = await search_objects(db, User, "ricardo shilly shally")
    assert users is not None
    assert len(users) == 0

@pytest.mark.asyncio
async def test_user_create_update_delete(db):
    user_create = UserCreate(**{"name": "TestUser3","email":"testuser3@example.com","password":"pass","confirm_password":"pass"})
    user_create.confirm_password = None
    user_db = await create_user(db, user_create)
    assert user_db in db
    user_update = UserUpdate(**{"name": "TestUser4"})
    updated_user_db = await update_obj(db, User, user_db.id, user_update)
    assert updated_user_db.name == "TestUser4"
    is_deleted = await delete_obj(db, User, user_db.id)
    assert is_deleted

@pytest.mark.asyncio
async def test_task_create_update_delete(db):
    user_db = db.execute(select(User)).scalars().first()
    task_create = TaskCreate(**{"title": "Test Task 2","description":"Test Task 2 descr","is_complete":True,"completed_at":datetime.now().isoformat()})  
    task_db = await create_task(db, task_create, user_db)
    assert task_db in db
    task_update = TaskUpdate(**{"title": "Test Task 3"})
    updated_task_db = await update_obj(db, Task, task_db.id, task_update)
    assert updated_task_db.title == "Test Task 3"
    is_deleted = await delete_obj(db, Task, task_db.id)
    assert is_deleted

# Test APIs
@pytest.fixture(scope="session")
def client(db):
    def get_test_db():
        try:
            yield db
        finally:
            pass
    app.dependency_overrides[get_db] = get_test_db
    yield TestClient(app)

@pytest.mark.asyncio
async def test_welcome(client, db):
    response = client.get("/api/")
    assert response.status_code == 200
    assert response.json()["message"] == "Welcome to dew"

""""
    POST /api/signup to create a user
    POST /api/login to log in a user, 
    GET /api/me to get the current logged in user, 
    GET /api/users/{user_id}/tasks to filter,search the user's tasks
    POST /api/users/{user_id}/tasks to create a task for this user
    PUT/PATCH /api/users/{user_id}/tasks/{task_id} to update this task
    DELETE /api/users/{user_id}/tasks/{task_id} to delete this task
    POST /api/users/{user_id}/chat to chat with llama that will have the context of this user's tasks
"""
