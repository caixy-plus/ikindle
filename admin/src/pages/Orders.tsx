import React from 'react';
import { Table, Button, Space, Tag } from 'antd';

const Orders: React.FC = () => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo' },
    { title: '用户ID', dataIndex: 'userId', key: 'userId' },
    { title: '金额', dataIndex: 'totalAmount', key: 'totalAmount', render: (v: number) => `¥${v}` },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (v: string) => {
        const color = v === 'COMPLETED' ? 'green' : v === 'PENDING' ? 'orange' : 'red';
        return <Tag color={color}>{v}</Tag>;
      },
    },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space>
          <Button type="link">详情</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>订单管理</h2>
      <Table columns={columns} dataSource={[]} rowKey="id" />
    </div>
  );
};

export default Orders;
