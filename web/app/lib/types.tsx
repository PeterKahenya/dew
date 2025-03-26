export type User = {
    id: string
    name: string
    email: string
    createdAt: string
    updatedAt?: string
}

export type UserSignup = {
    name: string
    email: string
    password: string
    confirmPassword: string
}

export type Login = {
    email: string
    password: string
}

export type Task = {
    id: string
    title: string
    description: string
    isComplete: boolean
    completedAt?: string
    user_id: string
    createdAt: string
    updatedAt?: string
}

export type TaskCreate = {
    title: string
    description: string
    isComplete: boolean
}

export type TaskUpdate = {
    title?: string
    description?: string
    isComplete?: boolean
    completedAt?: string
}

export type Chat = {
    role: string
    content: string
}

