export type User = {
    id: string
    name: string
    email: string
    created_at: string
    updated_at?: string
}

export type UserSignup = {
    name: string
    email: string
    password: string
    confirm_password: string
}

export type Login = {
    email: string
    password: string
}

export type Task = {
    id: string
    title: string
    description: string
    is_complete?: boolean
    completed_at?: string,
    user_id: string
    created_at: string
    updated_at?: string
}

export type TaskCreate = {
    title: string
    description: string
    is_complete: boolean
}

export type TaskUpdate = {
    title?: string
    description?: string
    is_complete?: boolean
    completed_at?: string
}

export type Chat = {
    role: string
    content: string
}

