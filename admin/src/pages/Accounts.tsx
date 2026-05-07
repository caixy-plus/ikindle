import React from 'react';
import { Table } from 'antd';

const Accounts: React.FC = () => {
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id' },
    { title: '用户ID', dataIndex: 'userId', key: 'userId' },
    { title: '余额', dataIndex: 'balance', key: 'balance' },
    { title: '冻结金额', dataIndex: 'frozenAmount', key: 'frozenAmount' },
    { title: '状态', dataIndex: 'status', key: 'status' },
  ];

  return (
    <div>
      <h2>账户管理</h2>
      <Table columns={columns} dataSource={[]} rowKey="id" />
    </div>
  );
};

export default Accounts;
