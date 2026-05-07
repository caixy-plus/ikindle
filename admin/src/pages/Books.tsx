import React from 'react';
import { Table, Button, Space, Tag } from 'antd';

const Books: React.FC = () => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: '书名', dataIndex: 'title', key: 'title' },
    { title: '作者', dataIndex: 'author', key: 'author' },
    { title: '价格', dataIndex: 'price', key: 'price', render: (v: number) => `¥${v}` },
    {
      title: '状态',
      dataIndex: 'published',
      key: 'published',
      render: (v: boolean) => v ? <Tag color="green">已上架</Tag> : <Tag>未上架</Tag>,
    },
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
      <h2>图书管理</h2>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary">新增图书</Button>
      </div>
      <Table columns={columns} dataSource={[]} rowKey="id" />
    </div>
  );
};

export default Books;
