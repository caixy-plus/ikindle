import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import MainLayout from './layouts/MainLayout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Books from './pages/Books';
import Categories from './pages/Categories';
import Tags from './pages/Tags';
import Orders from './pages/Orders';
import Users from './pages/Users';
import Roles from './pages/Roles';
import Menus from './pages/Menus';
import Accounts from './pages/Accounts';
import Dicts from './pages/Dicts';
import Config from './pages/Config';
import SyncTasks from './pages/SyncTasks';
import { useAuthStore } from './stores/auth';

const theme = {
  token: {
    colorPrimary: '#F5A623',
    borderRadius: 8,
  },
};

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const token = useAuthStore((s) => s.token);
  return token ? <>{children}</> : <Navigate to="/login" replace />;
};

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN} theme={theme}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/"
            element={
              <PrivateRoute>
                <MainLayout />
              </PrivateRoute>
            }
          >
            <Route index element={<Dashboard />} />
            <Route path="books" element={<Books />} />
            <Route path="categories" element={<Categories />} />
            <Route path="tags" element={<Tags />} />
            <Route path="orders" element={<Orders />} />
            <Route path="users" element={<Users />} />
            <Route path="roles" element={<Roles />} />
            <Route path="menus" element={<Menus />} />
            <Route path="accounts" element={<Accounts />} />
            <Route path="dicts" element={<Dicts />} />
            <Route path="config" element={<Config />} />
            <Route path="sync" element={<SyncTasks />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
};

export default App;
