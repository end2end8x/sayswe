import 'babel-polyfill';
import React, { Component } from 'react';
import { Admin, Delete, Resource } from 'admin-on-rest';

import './App.css';

import authClient from './authClient';
import sagas from './sagas';
import themeReducer from './themeReducer';
import Login from './Login';
import Layout from './Layout';
import Menu from './Menu';
import { Dashboard } from './dashboard';
import customRoutes from './routes';
import translations from './i18n';

import { VisitorList, VisitorEdit, VisitorDelete, VisitorIcon } from './visitors';
import { CommandList, CommandEdit, CommandIcon } from './commands';
import { ProductList, ProductCreate, ProductEdit, ProductIcon } from './products';
import { CategoryList, CategoryEdit, CategoryIcon } from './categories';
import { ReviewList, ReviewEdit, ReviewIcon } from './reviews';

import {RestClient, AuthClient} from './firebase/index';

import { UserList } from './users/Users';
import { MessageList } from './message/Message';


// import restClient from './restClient';
import fakeRestServer from './restServer';

var firebaseConfig = {
      apiKey: "AIzaSyCyfP2AdHbdjl0GWwhsmbf_Gv2FXnE4LQs",
      authDomain: "belive-fab1f.firebaseapp.com",
      databaseURL: "https://belive-fab1f.firebaseio.com",
      projectId: "belive-fab1f",
      storageBucket: "belive-fab1f.appspot.com",
      messagingSenderId: "804471267962"
};

const trackedResources = ['userChats', 'bl_session_message/wRHk0PB4Vd'];

class App extends Component {
    componentWillMount() {
        this.restoreFetch = fakeRestServer();
    }

    componentWillUnmount() {
        this.restoreFetch();
    }

    render() {
        const restFirebase = RestClient(trackedResources, firebaseConfig);
        
        return (

            <Admin
                title={"Says We"}
                restClient={restFirebase}
                customReducers={{ theme: themeReducer }}
                customSagas={sagas}
                customRoutes={customRoutes}
                authClient={authClient}
                dashboard={Dashboard}
                loginPage={Login}
                appLayout={Layout}
                menu={Menu}
                messages={translations}
                >
                <Resource name="userChats" list={UserList} />
                <Resource name="message" list={MessageList} />
                <Resource name="customers" list={VisitorList} edit={VisitorEdit} remove={VisitorDelete} icon={VisitorIcon} />
                <Resource name="commands" list={CommandList} edit={CommandEdit} remove={Delete} icon={CommandIcon} options={{ label: 'Orders' }}/>
                <Resource name="products" list={ProductList} create={ProductCreate} edit={ProductEdit} remove={Delete} icon={ProductIcon} />
                <Resource name="categories" list={CategoryList} edit={CategoryEdit} remove={Delete} icon={CategoryIcon} />
                <Resource name="reviews" list={ReviewList} edit={ReviewEdit} icon={ReviewIcon} />
            </Admin>

        );
    }
}

export default App;
