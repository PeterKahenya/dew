import uuid
from datetime import datetime
from sqlalchemy import Boolean, ForeignKey, String, create_engine
from sqlalchemy.dialects.mysql import pymysql
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship, sessionmaker
from pydantic_settings import BaseSettings
from pydantic import UUID4, BaseModel

class AppSettings(BaseSettings):
    mysql_driver: str = "mysql+pymysql"
    mysql_host: str
    mysql_port: int
    mysql_user: str
    mysql_password: str
    mysql_database: str

settings = AppSettings()

# Models and DB
DB_URL = f"{settings.mysql_driver}://{settings.mysql_user}:{settings.mysql_password}@{settings.mysql_host}:{settings.mysql_port}/{settings.mysql_database}"
engine = create_engine(DB_URL,pool_pre_ping=True, pool_size=10, max_overflow=20,pool_recycle=3600)

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

class Task(Model):
    __tablename__ = "tasks"
    id: Mapped[uuid.UUID] = mapped_column(primary_key=True,unique=True,default=uuid.uuid4)    
    title: Mapped[str] = mapped_column(String(100),unique=False)
    description: Mapped[str] = mapped_column(String(2000),unique=True)
    is_complete: Mapped[bool] = mapped_column(Boolean, default=False)
    completed_at: Mapped[datetime | None] = mapped_column()
    created_at: Mapped[datetime] = mapped_column(default=datetime.now)
    updated_at: Mapped[datetime | None] = mapped_column(onupdate=datetime.now)
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.id",ondelete="CASCADE"))
    user: Mapped[User] = relationship("User", back_populates="tasks")

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


