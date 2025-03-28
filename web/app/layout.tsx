import '@mantine/core/styles.css';
import '@mantine/dates/styles.css';
import { ColorSchemeScript, MantineProvider, createTheme, mantineHtmlProps } from '@mantine/core';
import { AuthProvider } from '@/contexts/auth';
import { Notifications } from '@mantine/notifications';

export const metadata = {
    title: 'Dew App',
    description: 'This is a tiny task/todo management app that supports adding/updating tasks, marking tasks as complete, and intelligently searching your tasks',
};

const theme = createTheme({
    fontFamily: 'Roboto, sans-serif',
    colors: {
        'ocean-blue': ['#7AD1DD', '#5FCCDB', '#44CADC', '#2AC9DE', '#1AC2D9', '#11B7CD', '#09ADC3', '#0E99AC', '#128797', '#147885'],
        'bright-pink': ['#F0BBDD', '#ED9BCF', '#EC7CC3', '#ED5DB8', '#F13EAF', '#F71FA7', '#FF00A1', '#E00890', '#C50E82', '#AD1374'],
    },
});

export default function RootLayout({
    children,
}: {
    children: React.ReactNode
}) {
    return (
        <html lang="en" {...mantineHtmlProps}>
            <head>
                <ColorSchemeScript defaultColorScheme="dark" />
            </head>
            <body>
                <MantineProvider theme={theme} defaultColorScheme='dark'>
                    <Notifications position='top-center' />
                    <AuthProvider>
                        {children}
                    </AuthProvider>
                </MantineProvider>
            </body>
        </html>
    )
}