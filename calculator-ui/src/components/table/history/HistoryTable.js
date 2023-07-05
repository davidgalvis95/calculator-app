import { EnhancedTable } from "../base/EnhancedTable";
import {
  sampleHistoryRows,
  sampleHistoryHeaders,
  sampleHistoryFilterConfig,
} from "../../../hooks/sampleData";
import useOperationsApi from "../../../hooks/useOperationsApi";
import { useEffect, useState } from "react";
import { baseApiUrl } from "../../../axios/CalculatorApi";

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 10;
const PREV_PAGE = "prev";
const NEXT_PAGE = "next";

const historyFilterConfig = [
  {
    display: "Operation Type",
    filterFieldName: "operationType",
    filterValue: "",
    values: ["ADDITION", "SUBTRACTION", "MULTIPLICATION", "DIVISION", "SQUARE_ROOT", "RANDOM_STRING"],
  },
];

export const HistoryTable = () => {
  const operationsService = useOperationsApi();
  const [response, setResponse] = useState({});
  useEffect(() => {
    setResponse(
      operationsService.getRecords(undefined, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)
    );
  }, []);

  const handleAppliedFilter = (additionalQueryParams) => {
    setResponse(
      operationsService.getRecords(
        undefined,
        DEFAULT_PAGE,
        DEFAULT_PAGE_SIZE,
        additionalQueryParams
      )
    );
  };



  const handlePageChange = (type) => {
    if(PREV_PAGE) {
      setResponse(
        operationsService.getRecords(
          response.prevPageToken.substring(baseApiUrl.length)
        )
      );
    }else {
      setResponse(
        operationsService.getRecords(
          response.nextPageToken.substring(baseApiUrl.length)
        )
      );
    }
  };



  return (
    <EnhancedTable
      headers={sampleHistoryHeaders}
      rowsData={response.records.map((r) => {
        return {
          id: r.id,
          userEmail: r.userEmail,
          amount: r.amount,
          userBalance: r.userBalance,
          operationType: r.operationType,
          operationState: r.operationState,
          dateTime: r.dateTime,
        };
      })}
      defaultSelectedSortField={"dateTime"}
      title={"Operations History"}
      filterConfig={historyFilterConfig}
      enableSelect={false}
      pagingData={{ totalRows: response.totalRecords, totalPages: response.totalPages }}
      handlePageChange={handlePageChange}
      handleAppliedFilter={handleAppliedFilter}
    />
  );
};
