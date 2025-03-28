"use client"
import { useChat } from "@/contexts/chat"
import { ActionIcon, Box, Container, Divider, List, ListItem, Loader, Paper, ScrollArea, Stack, Text, Textarea, TextInput, Title } from "@mantine/core"
import { IconArrowRight, IconSparkles } from "@tabler/icons-react"
import { Chat } from "lib/types"
import { useState } from "react"
import Markdown from 'react-markdown'
import { motion } from "motion/react"

function ChatBubble({ chat }: { chat: Chat }) {
    return <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        style={{
            alignSelf: chat.role === "assistant" ? "flex-start" : "flex-end",
            display: "inline-block",
            maxWidth: "75%",
            color: chat.role === "assistant" ? "#000" : "#000",
            borderRadius: "12px",
        }}
    >
        <Paper
            p="sm"
            mb="xs"
            bg={chat.role === "assistant" ? "#f5f5f5" : "#e6ffee"}
        >
            <Markdown>
                {chat.content}
            </Markdown>
        </Paper>
    </motion.div>
}


export default function Page() {
    const { chats, prompt, isThinking } = useChat()
    const [message, setMessage] = useState({
        role: "user",
        content: ""
    })

    async function handleSendMessage() {
        prompt(message)
        setMessage({ ...message, content: "" })
    }

    return <div>
        <Title c={"teal"}>
            <IconSparkles stroke={1.5} style={{ paddingRight: 5 }} />
            Chat
        </Title>
        <Divider
            my="xs"
            variant="dashed"
            labelPosition="center"
            label={
                <>
                    {"🤓"} <Box ml={5}>Ask away...</Box>
                </>
            }
        />
        <Container size="xl" style={{ display: "flex", flexDirection: "column", height: "1000px" }}>
            <ScrollArea style={{ flex: 1, padding: "10px", border: "1px solid #ddd", borderRadius: "8px", display: "flex", flexDirection: "column", alignItems: "stretch" }}>
                <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-start", gap: "8px" }}>
                    {chats.map((chat: Chat, id: number) => <ChatBubble key={id} chat={chat} />)}
                    {isThinking && <Loader type="dots" />}
                </div>
            </ScrollArea>
            <Paper>
                <Textarea
                    radius="lg"
                    size="md"
                    placeholder="Ask about your tasks"
                    rightSectionWidth={42}
                    onKeyDown={(e) => {
                        if (e.key === "Enter" && message.content.trim().length > 0) {
                            handleSendMessage()
                        }
                    }}
                    rightSection={
                        <ActionIcon onClick={handleSendMessage} size={32} radius="xl" color={"green"} variant="filled">
                            <IconArrowRight size={18} stroke={1.5} />
                        </ActionIcon>
                    }
                    value={message.content}
                    onChange={(e) => setMessage({ ...message, content: e.target.value })}
                />
            </Paper>
        </Container>
    </div>
}