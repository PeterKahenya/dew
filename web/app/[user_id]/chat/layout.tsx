
"use client"
import { ChatProvider } from "@/contexts/chat";
import { use } from "react";

export default function ChatLayout({ children, params }: { children: React.ReactNode, params: Promise<{ user_id: string }> }) {
    const { user_id } = use(params)
    return <ChatProvider user_id={user_id}>
        {children}
    </ChatProvider>
}