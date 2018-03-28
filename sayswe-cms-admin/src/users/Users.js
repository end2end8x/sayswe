import React from 'react';
import { List, Datagrid, EmailField, TextField, BooleanField } from 'admin-on-rest';

export const UserList = (props) => (
    <List title="All users" {...props}>
        <Datagrid>
            <TextField source="uid" />
            <TextField source="name" />
            <TextField source="updatedAt" />
            <BooleanField source="emailVerified" />
            <EmailField source="email" />
        </Datagrid>
    </List>
);