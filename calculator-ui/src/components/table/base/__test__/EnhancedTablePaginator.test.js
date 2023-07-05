import React from "react";
import { render, fireEvent } from "@testing-library/react";
import { EnhancedTablePaginator } from "./EnhancedTablePaginator";

describe("EnhancedTablePaginator", () => {
  it("should render the component correctly", () => {
    const handleChangePage = jest.fn();
    const rowsPerPage = 10;
    const page = 0;
    const totalPages = 5;
    const totalRows = 50;

    const { getByText, getByTestId } = render(
      <EnhancedTablePaginator
        rowsPerPage={rowsPerPage}
        page={page}
        totalPages={totalPages}
        totalRows={totalRows}
        handleChangePage={handleChangePage}
      />
    );

    // Verify the rendered elements
    expect(getByText(`Rows Per Page: ${rowsPerPage}`)).toBeInTheDocument();
    expect(
      getByText(`${page * rowsPerPage + 1}-${rowsPerPage * (page + 1)} of ${totalRows}`)
    ).toBeInTheDocument();

    const prevButton = getByTestId("pagingPrevButton");
    const nextButton = getByTestId("pagingNextButton");

    expect(prevButton).toBeInTheDocument();
    expect(nextButton).toBeInTheDocument();

    // Verify button click behavior
    fireEvent.click(prevButton);
    expect(handleChangePage).toHaveBeenCalledWith("prev");

    fireEvent.click(nextButton);
    expect(handleChangePage).toHaveBeenCalledWith("next");
  });

  it("should disable prev/next buttons when appropriate", () => {
    const handleChangePage = jest.fn();
    const rowsPerPage = 10;
    const page = 0;
    const totalPages = 5;
    const totalRows = 50;

    const { getByTestId } = render(
      <EnhancedTablePaginator
        rowsPerPage={rowsPerPage}
        page={page}
        totalPages={totalPages}
        totalRows={totalRows}
        handleChangePage={handleChangePage}
      />
    );

    const prevButton = getByTestId("pagingPrevButton");
    const nextButton = getByTestId("pagingNextButton");

    expect(prevButton).toBeDisabled();
    expect(nextButton).not.toBeDisabled();

    fireEvent.click(nextButton);

    expect(prevButton).not.toBeDisabled();
    expect(nextButton).not.toBeDisabled();

    fireEvent.click(nextButton);

    expect(prevButton).not.toBeDisabled();
    expect(nextButton).toBeDisabled();
  });
});