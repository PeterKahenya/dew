import axios from "axios";
import { Chat } from "lib/types";
import { createContext, useContext, useState } from "react";

const ChatContext = createContext(null)
const API_BASE = "http://localhost:8000/api"

export function ChatProvider({ children, user_id }: { children: React.ReactNode, user_id: string }) {
    const [chats, setChats] = useState<Chat[]>([{
        role: "system",
        content: "Welcome to dew Chat. How may I help you today?"
    }])

    const prompt = async (chat: Chat) => {
        const resp = await axios.post(`${API_BASE}/users/${user_id}/chat`, chat, { withCredentials: true })
        if (resp.status === 200 && resp.data) setChats([...chats, resp.data])
    }

    return <ChatContext.Provider value={{ chats, prompt }}>
        {children}
    </ChatContext.Provider>
}

export const useChat = () => useContext(ChatContext)