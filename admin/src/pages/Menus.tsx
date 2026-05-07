import React from 'react';
import { Table, Button, Space } from 'antd';

const Menus: React.FC = () => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: '标识', dataIndex: 'menuKey', key: 'menuKey' },
    { title: '名称', dataIndex: 'text', key: 'text' },
    { title: '路径', dataIndex: 'path', key: 'path' },
    { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder' },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space>
          <Button type="link">编辑</Button>
          <Button type="link" danger>删除</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>菜单管理</h2>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary">新增菜单</Button>
      </div>
      <Table columns={columns} dataSource={[]} rowKey="id" />
    </div>
  );
};

export default Menus;
