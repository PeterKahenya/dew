
import classes from './SideBar.module.css';
import { IconBellRinging, IconLogout } from "@tabler/icons-react"
import { Code, Group, Title } from '@mantine/core';
import Link from 'next/link';
import { useAuth } from '@/contexts/auth';
import { useRouter } from 'next/navigation';

export default function SideBar({ active, setActive }: { active: string, setActive: (t: string) => void }) {
    const { logout } = useAuth()
    const router = useRouter()

    const handleLogout = async (event) => {
        event.preventDefault()
        const response = await logout()
        if (response.message === "Logged Out") {
            router.push("/login")
        }
    }

    return (
        <nav className={classes.navbar}>
            <div className={classes.navbarMain}>
                <Group className={classes.header} justify="space-between">
                    <Title size={28} >Dew</Title>
                    <Code fw={700}>v1.0.0</Code>
                </Group>
                <Link
                    className={classes.link}
                    data-active={active === "today" || undefined}
                    href="today"
                    onClick={(e) => {
                        setActive("today");
                    }}
                >
                    <IconBellRinging className={classes.linkIcon} stroke={1.5} />
                    <span>Today</span>
                </Link>
                <Link
                    className={classes.link}
                    data-active={active === "tasks" || undefined}
                    href="tasks"
                    onClick={(e) => {
                        setActive("tasks");
                    }}
                >
                    <IconBellRinging className={classes.linkIcon} stroke={1.5} />
                    <span>All Tasks</span>
                </Link>
                <Link
                    className={classes.link}
                    data-active={active === "chat" || undefined}
                    href="chat"
                    onClick={(e) => {
                        setActive("chat");
                    }}
                >
                    <IconBellRinging className={classes.linkIcon} stroke={1.5} />
                    <span>Smart Chat</span>
                </Link>
            </div>
            <div className={classes.footer}>
                <a href="#" className={classes.link} onClick={handleLogout}>
                    <IconLogout className={classes.linkIcon} stroke={1.5} />
                    <span>Logout</span>
                </a>
            </div>
        </nav>
    )
}