import { GrFormNext } from "react-icons/gr";
import { GrFormPrevious } from "react-icons/gr";
import "./EnhancedTablePaginator.css";

export const EnhancedTablePaginator = ({
  rowsPerPage,
  page,
  totalPages,
  totalRows,
  handleChangePage,
}) => {
  const PREV_PAGE = "prev";
  const NEXT_PAGE = "next";
  const handleSwitchPage = (type) => {
    handleChangePage(type);
  };

  return (
    <div className="pagingContainer">
      <div className="pagingElement">Rows Per Page: {rowsPerPage}</div>
      <div className="pagingElement">
        {page * rowsPerPage + 1}-{rowsPerPage * (page + 1)} of {totalRows}
      </div>
      <button
        className="pagingPrevButton"
        disabled={page === 0}
        onClick={(e) => handleSwitchPage(PREV_PAGE)}
      >
        <GrFormPrevious />
      </button>

      <button
        className="pagingNextButton"
        disabled={page === totalPages}
        onClick={(e) => handleSwitchPage(NEXT_PAGE)}
      >
        <GrFormNext />
      </button>
    </div>
  );
};
