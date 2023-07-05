import { EnhancedTable } from "../base/EnhancedTable";

function createData(id, email, status, balance, roles) {
  return {
    id,
    email,
    status,
    balance,
    roles,
  };
}

const rows = [
  createData("Cupcake", 305, "Active", 67, 4.3),
  createData("Donut", 452, "Active", 51, 4.9),
  createData("Eclair", 262, "Active", 24, 6.0),
  createData("Frozen yoghurt", 6.0, "Inactive", 24, 4.0),
  createData("Gingerbread", 356, "Active", 49, 3.9),
  createData("Honeycomb", 408, "Active", 87, 6.5),
  createData("Ice cream sandwich", 237, "Active", 37, 4.3),
  createData("Jelly Bean", 375, "Active", 94, 0.0),
  createData("KitKat", 518, "Active", 65, 7.0),
  createData("Lollipop", 392, "Inactive", 98, 0.0),
  createData("Marshmallow", "Active", 0, 81, 2.0),
  createData("Nougat", 360, "Active", 9, 37.0),
  createData("Oreo", 437, "Active", 63, 4.0),
];

const headers = [
  {
    id: "id",
    numeric: false,
    disablePadding: true,
    label: "Id",
  },
  {
    id: "email",
    numeric: false,
    disablePadding: false,
    label: "Email",
  },
  {
    id: "status",
    numeric: false,
    disablePadding: false,
    label: "Status",
  },
  {
    id: "balance",
    numeric: true,
    disablePadding: false,
    label: "Balance",
  },
  {
    id: "roles",
    numeric: true,
    disablePadding: false,
    label: "Roles",
  },
];

const processSelected = (selected) => {

  //selected ids
  selected.forEach()
}

export const UsersTable = () => {
  return (
    <EnhancedTable
      headers={headers}
      rowsData={rows}
      defaultSelectedSortField={"email"}
    />
  );
};
