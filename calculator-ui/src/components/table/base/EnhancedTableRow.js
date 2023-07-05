import * as React from "react";
import TableCell from "@mui/material/TableCell";
import TableRow from "@mui/material/TableRow";
import Checkbox from "@mui/material/Checkbox";
import PropTypes from "prop-types";

export const EnhancedTableRow = (props) => {
  const { selected, rowData, handleSelectClick, labelId, showCheckBox } = props;

  return (
    <TableRow
      hover
      onClick={(event) => handleSelectClick(event, rowData.id)}
      role={showCheckBox ? "checkbox" : null}
      aria-checked={showCheckBox ? selected : null}
      tabIndex={-1}
      selected={null}
      sx={{ cursor: "pointer" }}
    >
      {showCheckBox ? (
        <TableCell padding="checkbox">
          <Checkbox
            color="primary"
            checked={selected}
            inputProps={{
              "aria-labelledby": labelId,
            }}
          />
        </TableCell>
      ) : null}

      {Object.entries(rowData).map(([cellKey, cellValue], index) => {
        if (cellKey === "id") {
          return (
            <TableCell
              sx={{ paddingRight: "20px" }}
              key={cellKey}
              component="th"
              id={labelId}
              scope="row"
              padding="none"
            >
              {rowData.id}
            </TableCell>
          );
        } else {
          return (
            <TableCell sx={{ paddingRight: "20px" }} key={cellKey} align="right">
              {cellValue}
            </TableCell>
          );
        }
      })}
    </TableRow>
  );
};

EnhancedTableRow.propTypes = {
  selected: PropTypes.bool.isRequired,
  rowData: PropTypes.object.isRequired,
  handleSelectClick: PropTypes.func.isRequired,
  labelId: PropTypes.string.isRequired,
};
