"use client"

import { useAuth } from "@/contexts/auth"
import { useRouter } from "next/navigation"
import { useState } from "react"
import {
    Anchor,
    Button,
    Container,
    Paper,
    PasswordInput,
    Text,
    TextInput,
    Title,
} from '@mantine/core';

import classes from './login.module.css';
import { notifications } from '@mantine/notifications';


export default function Page() {
    const { login, user, getProfile } = useAuth()
    const router = useRouter()
    const [userLogin, setUserLogin] = useState({
        email: "",
        password: ""
    })

    async function handleLogin(e) {
        e.preventDefault()
        const response = await login(userLogin)
        console.log(response)
        if (response.message === "Logged In") {
            const profile = await getProfile()
            router.push(`/${profile.id}/today`)
        } else {
            notifications.show({
                title: 'Login Error',
                w: 500,
                message: response.detail.message,
                color: 'red',
                position: "top-center"
            })
        }
    }

    return <div>
        <Container size={420} my={40}>
            <h1 style={{ textAlign: "center" }}>Dew</h1>
            <Title ta="center" className={classes.title}>
                Welcome back!
            </Title>
            <Text c="dimmed" size="sm" ta="center" mt={5}>
                Do not have an account yet?{' '}
                <Anchor href="/signup" size="sm">
                    Create account
                </Anchor>
            </Text>
            <Paper withBorder shadow="md" p={30} mt={30} radius="md">
                <TextInput
                    type="email"
                    label="Email"
                    required
                    placeholder="you@mail.com"
                    value={userLogin.email}
                    onChange={(e) => setUserLogin({ ...userLogin, email: e.target.value })}
                />
                <PasswordInput
                    mt="md"
                    label="Password"
                    placeholder="*************"
                    required
                    value={userLogin.password}
                    onChange={(e) => setUserLogin({ ...userLogin, password: e.target.value })}
                />
                <Button fullWidth mt="xl" onClick={handleLogin}>
                    Sign in
                </Button>
            </Paper>
        </Container>
    </div>
}