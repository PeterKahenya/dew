"use client"
import { useChat } from "@/contexts/chat"
import { Chat } from "lib/types"
import { useState } from "react"

function ChatBubble({ chat }: { chat: Chat }) {
    return <div>
        <strong>{chat.role}</strong>
        <p>{chat.content}</p>
    </div>
}


export default function Page() {
    const { chats, prompt } = useChat()
    const [message, setMessage] = useState({
        role: "user",
        content: ""
    })

    return <div>
        <h1>AI Chat</h1>
        {chats.map((chat: Chat, id: number) => <ChatBubble key={id} chat={chat} />)}
        <div>
            <input type="text" value={message.content} onChange={(e) => setMessage({ ...message, content: e.target.value })} placeholder="Ask about your tasks" />
            <button onClick={() => prompt(message)}>Send</button>
        </div>
    </div>
}