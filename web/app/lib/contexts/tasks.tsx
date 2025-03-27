import { createContext, useContext, useState } from "react";
import useSWR, { mutate } from "swr";
import axios from "axios";
import { TaskCreate, TaskUpdate } from "lib/types";

export const TasksContext = createContext(null);
const API_BASE = "http://localhost:8000/api";

export const fetcher = async (url: string, params?: object) => {
    return axios.get(`${API_BASE}${url}`, { params, withCredentials: true }).then(resp => resp.data)
}

export function TasksProvider({ children, user_id }: { children: React.ReactNode, user_id: string }) {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const defaultParams = { created_at__gte: today.toISOString() };
    const [currentFilter, setCurrentFilter] = useState<object | null>(defaultParams);
    const { data, error, isLoading } = useSWR([`/users/${user_id}/tasks`, currentFilter || defaultParams], ([url, params]) => fetcher(url, params))

    const createTask = async (taskCreate: TaskCreate) => {
        const resp = await axios.post(`${API_BASE}/users/${user_id}/tasks`, taskCreate, { withCredentials: true })
        await mutate([`/users/${user_id}/tasks`, currentFilter || defaultParams]);
        return resp
    }

    const updateTask = async (task_id: string, taskUpdate: TaskUpdate) => {
        const resp = await axios.put(`${API_BASE}/users/${user_id}/tasks/${task_id}/`, taskUpdate, { withCredentials: true })
        await mutate([`/users/${user_id}/tasks`, currentFilter || defaultParams]);
        return resp
    }

    const deleteTask = async (task_id: string) => {
        const resp = await axios.delete(`${API_BASE}/users/${user_id}/tasks/${task_id}/`, { withCredentials: true })
        await mutate([`/users/${user_id}/tasks`, currentFilter || defaultParams]);
        return resp
    }

    const filterTasks = async (params: object) => {
        try {
            setCurrentFilter(params);
            const resp = await axios.get(`${API_BASE}/users/${user_id}/tasks`, { params, withCredentials: true });
            await mutate([`/users/${user_id}/tasks`, params], resp.data, { revalidate: false });
        } catch (error) {
            console.error("Error filtering tasks:", error);
        }
    };

    return <TasksContext.Provider value={{
        tasks: data,
        isLoading,
        filterTasks,
        createTask,
        updateTask,
        deleteTask
    }}>
        {isLoading ? <p>Loading ...</p> : children}
    </TasksContext.Provider>
}

export const useTasks = () => useContext(TasksContext)