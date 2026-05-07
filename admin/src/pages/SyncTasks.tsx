import React from 'react';
import { Table, Tag, Button, Space } from 'antd';

const SyncTasks: React.FC = () => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: '用户ID', dataIndex: 'userId', key: 'userId' },
    { title: '图书ID', dataIndex: 'bookId', key: 'bookId' },
    { title: '目标邮箱', dataIndex: 'targetEmail', key: 'targetEmail' },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (v: string) => {
        const color = v === 'COMPLETED' ? 'green' : v === 'PENDING' ? 'orange' : 'red';
        return <Tag color={color}>{v}</Tag>;
      },
    },
    { title: '重试次数', dataIndex: 'retryCount', key: 'retryCount' },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space>
          <Button type="link">重试</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>同步任务监控</h2>
      <Table columns={columns} dataSource={[]} rowKey="id" />
    </div>
  );
};

export default SyncTasks;
