import axios from "axios";
import { Chat } from "lib/types";
import { createContext, useContext, useState } from "react";

const ChatContext = createContext(null)
const API_BASE = "http://localhost:8000/api"

export function ChatProvider({ children, user_id }: { children: React.ReactNode, user_id: string }) {
    const [chats, setChats] = useState<Chat[]>([{
        role: "assistant",
        content: "Welcome to dew Chat. How may I help you today?"
    }])

    const [isThinking, setIsThinking] = useState(false)

    const prompt = async (chat: Chat) => {
        setIsThinking(true)
        setChats([...chats, { role: "user", content: chat.content }])
        axios.post(`${API_BASE}/users/${user_id}/chat`, chat, { withCredentials: true }).then(resp => {
            if (resp.status === 200 && resp.data) {
                setChats(chats => [...chats, resp.data])
            }
        }).finally(() => {
            setIsThinking(false)
        })

    }

    return <ChatContext.Provider value={{ chats, prompt, isThinking }}>
        {children}
    </ChatContext.Provider>
}

export const useChat = () => useContext(ChatContext)