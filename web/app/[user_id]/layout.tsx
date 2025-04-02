"use client"
import { useAuth } from "@/contexts/auth";
import { TasksProvider } from "@/contexts/tasks";
import { usePathname, useRouter } from "next/navigation";
import { use, useEffect, useState } from "react";
import { AppShell, Burger, Code, Group, Loader, Title } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import classes from './layout.module.css';
import Link from "next/link";
import { IconCalendar, IconList, IconLogout, IconSparkles } from "@tabler/icons-react";

export default function UserHomeLayout({ children, params }: { children: React.ReactNode, params: Promise<{ user_id: string }> }) {
    const { user_id } = use(params)
    const { user, isLoading, logout } = useAuth()
    const pathname = usePathname().split("/")[2]
    const [activeTab, setActiveTab] = useState(pathname)
    const router = useRouter()
    const [opened, { toggle }] = useDisclosure();


    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login");
        }
    }, [user, isLoading, router])

    const handleLogout = async (event) => {
        event.preventDefault()
        const response = await logout()
        if (response.message === "Logged Out") {
            router.push("/login")
        }
    }

    return <TasksProvider user_id={user_id}>
        <AppShell
            header={{ height: { base: 60, md: 70, lg: 80 } }}
            navbar={{
                width: { base: 200, md: 300, lg: 400 },
                breakpoint: 'sm',
                collapsed: { mobile: !opened },
            }}
            padding="md"
        >
            <AppShell.Header>
                <Group h="100%" px="md">
                    <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
                    <Group justify="space-between">
                        <Title size={40} c={'green'} >Dew</Title>
                        <Code fw={700}>v1.0.1</Code>
                    </Group>
                </Group>
            </AppShell.Header>
            <AppShell.Navbar p="md">
                <div className={classes.navbarMain}>
                    <a
                        className={classes.link}
                        data-active={activeTab === "today" || undefined}
                        href={`/${user?.id}/today`}
                        onClick={(e) => {
                            setActiveTab("today");
                        }}
                    >
                        <IconCalendar className={classes.linkIcon} stroke={1.5} />
                        <span>Today</span>
                    </a>
                    <a
                        className={classes.link}
                        data-active={activeTab === "tasks" || undefined}
                        href={`/${user?.id}/tasks`}
                        onClick={(e) => {
                            setActiveTab("tasks");
                        }}
                    >
                        <IconList className={classes.linkIcon} stroke={1.5} />
                        <span>All Tasks</span>
                    </a>
                    <a
                        className={classes.link}
                        data-active={activeTab === "chat" || undefined}
                        href={`/${user?.id}/chat`}
                        onClick={(e) => {
                            setActiveTab("chat");
                        }}
                    >
                        <IconSparkles className={classes.linkIcon} stroke={1.5} />
                        <span>Chat</span>
                    </a>
                </div>
                <div className={classes.footer}>
                    <a href="#" className={classes.link} onClick={handleLogout}>
                        <IconLogout className={classes.linkIcon} stroke={1.5} />
                        <span>Logout</span>
                    </a>
                </div>
            </AppShell.Navbar>
            <AppShell.Main>{isLoading ? <Loader size={30} /> : children}</AppShell.Main>
        </AppShell>
    </TasksProvider>
}