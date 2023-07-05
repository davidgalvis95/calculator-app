import { useEffect, useState } from "react";
import Box from "@mui/material/Box";
import Table from "@mui/material/Table";
import TableContainer from "@mui/material/TableContainer";
import TablePagination from "@mui/material/TablePagination";
import Paper from "@mui/material/Paper";
import Sidebar from "../../sidebar/Sidebar";
import PropTypes from "prop-types";
import { EnhancedTableToolbar } from "./EnhancedTableToolBar";
import { EnhancedTableHead } from "./EnhancedTableHead";
import { EnhancedTableBody } from "./EnhancedTableBody";

const descendingComparator = (a, b, orderBy) => {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
};

const getComparator = (order, orderBy) => {
  return order === "desc"
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
};

const stableSort = (array, comparator) => {
  const stabilizedThis = array.map((el, index) => [el, index]);
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) {
      return order;
    }
    return a[1] - b[1];
  });
  return stabilizedThis.map((el) => el[0]);
};

const DEFAULT_ROWS_PER_PAGE = 10;

export const EnhancedTable = (props) => {
  const {
    headers,
    rowsData,
    defaultSelectedSortField,
    title,
    filterConfig,
    enableSelect,
  } = props;
  const [order, setOrder] = useState("asc");
  const [orderBy, setOrderBy] = useState(defaultSelectedSortField);
  const [selected, setSelected] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(DEFAULT_ROWS_PER_PAGE);
  const [statusFilter, setStatusFilter] = useState(filterConfig);
  const [filteredRows, setFilteredRows] = useState(rowsData);
  const [emptyRows, setEmptyRows] = useState(0);
  const [visibleRows, setVisibleRows] = useState(rowsData);

  useEffect(() => {
    if (statusFilter) {
      setFilteredRows(rowsData.filter((row) => rowMatchesFilter(row)));
    } else {
      setFilteredRows(rowsData);
    }

    if (page > 0) {
      setEmptyRows(Math.max(0, (1 + page) * rowsPerPage - rowsData.length));
    } else {
      setEmptyRows(0);
    }
  }, [statusFilter, page, rowsPerPage, rowsData]);

  useEffect(() => {
    const newVisibleRows = stableSort(
      filteredRows,
      getComparator(order, orderBy)
    ).slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
    setVisibleRows(newVisibleRows);
  }, [filteredRows, order, orderBy, page, rowsPerPage]);

  const rowMatchesFilter = (row) => {
    if (statusFilter.length > 0) {
      const numberOfCriteriaToMatch = statusFilter.length;
      const countOfMatchedCriteria = statusFilter.filter((filter) => {
        if (filter.filterValue !== "") {
          const rowValue = row[filter.filterFieldName];
          const stringifiedRowValue =
            rowValue && typeof rowValue === "string"
              ? rowValue.toLowerCase()
              : rowValue.toString().toLowerCase();
          return stringifiedRowValue === filter.filterValue;
        } else {
          return true;
        }
      }).length;
      return numberOfCriteriaToMatch === countOfMatchedCriteria;
    }
    return true;
  };

  const handleSelectAllClick = (event) => {
    if (event.target.checked) {
      const newSelected = rowsData.map((row) => row.id);
      setSelected(newSelected);
      return;
    }
    setSelected([]);
  };

  const handleSelectClick = (event, id) => {
    const selectedIndex = selected.indexOf(id);
    let newSelected = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, id);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(selected.slice(1));
    } else if (selectedIndex === selected.length - 1) {
      newSelected = newSelected.concat(selected.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selected.slice(0, selectedIndex),
        selected.slice(selectedIndex + 1)
      );
    }
    setSelected(newSelected);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleStatusFilterChange = (event, filter) => {
    const statusFilterCopy = [...statusFilter];
    const filterFieldNamePredicate = (f) =>
      f.filterFieldName === filter.filterFieldName;
    const filterElementIndex = statusFilterCopy.findIndex(
      filterFieldNamePredicate
    );
    const filterElement = statusFilterCopy.find(filterFieldNamePredicate);
    const filterElementCopy = { ...filterElement };
    filterElementCopy.filterValue = event.target.value.toLowerCase();
    statusFilterCopy[filterElementIndex] = filterElementCopy;
    setStatusFilter(statusFilterCopy);
  };

  const handleRequestSort = (event, property) => {
    const isAsc = orderBy === property && order === "asc";
    setOrder(isAsc ? "desc" : "asc");
    setOrderBy(property);
  };

  console.log(visibleRows.length);

  return (
    <div>
      <h2>{title}</h2>
      <Box sx={{ width: "100%"}}>
        <Paper sx={{ width: "100%", mb: 2 }}>
          <EnhancedTableToolbar
            numSelected={selected.length}
            handleStatusFilterChange={handleStatusFilterChange}
            filters={statusFilter}
          />

          <TableContainer>
            <Table
              sx={{ minWidth: 1300 }}
              aria-labelledby="tableTitle"
              size={"medium"}
            >
              <EnhancedTableHead
                headers={headers}
                numSelected={selected.length}
                order={order}
                orderBy={orderBy}
                onSelectAllClick={handleSelectAllClick}
                onRequestSort={handleRequestSort}
                rowCount={rowsData.length}
                enableSelect={enableSelect}
              />
              <EnhancedTableBody
                rowsData={visibleRows}
                handleSelectClick={handleSelectClick}
                selected={selected}
                emptyRows={emptyRows}
                enableSelect={enableSelect}
              />
            </Table>
          </TableContainer>
          <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={filteredRows.length}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangeRowsPerPage}
          />
        </Paper>
      </Box>
      <Sidebar />
    </div>
  );
};

EnhancedTable.propTypes = {
  headers: PropTypes.arrayOf(
    PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.string.isRequired,
        numeric: PropTypes.bool.isRequired,
        disablePadding: PropTypes.bool.isRequired,
        label: PropTypes.string.isRequired,
      })
    )
  ),
  rowsData: PropTypes.arrayOf(
    PropTypes.arrayOf(PropTypes.shape(PropTypes.object))
  ),
  defaultSelectedSortField: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
  filterConfig: PropTypes.arrayOf(
    PropTypes.shape({
      display: PropTypes.string.isRequired,
      filterFieldName: PropTypes.string.isRequired,
      filterValue: PropTypes.string.isRequired,
      values: PropTypes.arrayOf(PropTypes.string),
    })
  ),
};
