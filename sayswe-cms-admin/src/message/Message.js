import React from 'react';
import { List, Datagrid, EmailField, TextField, BooleanField, ImageField } from 'admin-on-rest';

export const MessageList = (props) => (
    <List title="Messages" {...props}>
        <Datagrid>
            <TextField source="name" />
            <ImageField source="img_url_large" />
            <TextField source="content" />
            <TextField source="type" />
            <TextField source="senderId" />
            <TextField source="updatedAt" />
        </Datagrid>
    </List>
);