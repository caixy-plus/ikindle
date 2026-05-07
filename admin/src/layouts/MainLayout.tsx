import React from 'react';
import { Layout, Menu, Avatar, Dropdown, Space, theme } from 'antd';
import {
  DashboardOutlined,
  BookOutlined,
  TagsOutlined,
  ShoppingCartOutlined,
  TeamOutlined,
  SafetyOutlined,
  SettingOutlined,
  MenuOutlined,
  AccountBookOutlined,
  FileTextOutlined,
  SyncOutlined,
  LogoutOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../stores/auth';

const { Header, Sider, Content } = Layout;

const menuItems = [
  { key: '/', icon: <DashboardOutlined />, label: '仪表盘' },
  { key: '/books', icon: <BookOutlined />, label: '图书管理' },
  { key: '/categories', icon: <TagsOutlined />, label: '分类管理' },
  { key: '/tags', icon: <TagsOutlined />, label: '标签管理' },
  { key: '/orders', icon: <ShoppingCartOutlined />, label: '订单管理' },
  { key: '/users', icon: <TeamOutlined />, label: '用户管理' },
  { key: '/roles', icon: <SafetyOutlined />, label: '角色权限' },
  { key: '/menus', icon: <MenuOutlined />, label: '菜单管理' },
  { key: '/accounts', icon: <AccountBookOutlined />, label: '账户管理' },
  { key: '/dicts', icon: <FileTextOutlined />, label: '字典管理' },
  { key: '/config', icon: <SettingOutlined />, label: '系统参数' },
  { key: '/sync', icon: <SyncOutlined />, label: '同步任务' },
];

const MainLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, clearAuth } = useAuthStore();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: () => {
        clearAuth();
        navigate('/login');
      },
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light" breakpoint="lg" collapsedWidth="0">
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20, fontWeight: 'bold', color: '#F5A623' }}>
          iKindle Admin
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', background: colorBgContainer, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <span style={{ fontSize: 16, fontWeight: 500 }}>管理后台</span>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Space style={{ cursor: 'pointer' }}>
              <Avatar icon={<UserOutlined />} />
              <span>{user?.nickname || user?.username || 'Admin'}</span>
            </Space>
          </Dropdown>
        </Header>
        <Content style={{ margin: 24, padding: 24, background: colorBgContainer, borderRadius: borderRadiusLG }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
