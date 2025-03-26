"use client"
import { Anchor, Button, Container, Paper, PasswordInput, Text, TextInput, Title } from "@mantine/core";
import { useState } from "react";

import classes from "./signup.module.css"
import { useAuth } from "@/contexts/auth";
import { useRouter } from "next/navigation";
import { notifications } from "@mantine/notifications";
import { UserSignup } from "lib/types";

export default function Page() {
    const { signup } = useAuth()
    const [user, setUser] = useState<UserSignup>({
        name: "",
        email: "",
        password: "",
        confirmPassword: ""
    })
    const router = useRouter()


    async function handleSignup(e: { preventDefault: () => void; }) {
        console.log("Trying to signup ", user)
        e.preventDefault()
        const response = await signup(user)
        console.log(response)
        if (response.status === 201) {
            router.push(`/login`)
        } else {
            notifications.show({
                title: 'Signup Error',
                w: 500,
                message: JSON.stringify(response.detail.map(d => `${d.loc} ${d.msg} ${d.type}`)),
                color: 'red',
                position: "top-center"
            })
        }
    }



    return <Container size={420} my={40}>
        <h1>Dew</h1>
        <Title ta="center" className={classes.title}>
            Get Started
        </Title>
        <Text c="dimmed" size="sm" ta="center" mt={5}>
            Already have an account?{' '}
            <Anchor href="/login" size="sm">
                Login
            </Anchor>
        </Text>
        <Paper withBorder shadow="md" p={30} mt={30} radius="md">
            <TextInput
                label="Name"
                required
                placeholder="Your first name"
                value={user.name}
                onChange={(e) => setUser({ ...user, name: e.target.value })}
            />
            <TextInput
                label="Email"
                type="email"
                required
                placeholder="you@mail.com"
                value={user.email}
                onChange={(e) => setUser({ ...user, email: e.target.value })}
            />
            <PasswordInput
                mt="md"
                label="Password"
                placeholder="*************"
                required
                value={user.password}
                onChange={(e) => setUser({ ...user, password: e.target.value })}
            />
            <PasswordInput
                mt="md"
                label="Confirm Password"
                placeholder="*************"
                required
                value={user.confirmPassword}
                onChange={(e) => setUser({ ...user, confirmPassword: e.target.value })}
            />

            <Button fullWidth mt="xl" onClick={handleSignup}>
                Register
            </Button>
        </Paper>
    </Container>
}