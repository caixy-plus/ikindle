import React from 'react';
import { Table, Button, Space, Switch } from 'antd';

const Users: React.FC = () => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '昵称', dataIndex: 'nickname', key: 'nickname' },
    { title: '邮箱', dataIndex: 'email', key: 'email' },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      render: (v: boolean) => <Switch checked={v} />,
    },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space>
          <Button type="link">编辑</Button>
          <Button type="link">分配角色</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>用户管理</h2>
      <Table columns={columns} dataSource={[]} rowKey="id" />
    </div>
  );
};

export default Users;
