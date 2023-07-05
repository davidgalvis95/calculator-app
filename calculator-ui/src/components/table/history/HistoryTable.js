import { EnhancedTable } from "../base/EnhancedTable";
import { sampleHistoryRows, sampleHistoryHeaders, sampleHistoryFilterConfig } from "./sampleData";

export const HistoryTable = () => {
  return (
    <EnhancedTable
      headers={sampleHistoryHeaders}
      rowsData={sampleHistoryRows.map((r) => {
        return {
          operationId: r.id,
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
      filterConfig={sampleHistoryFilterConfig}
      enableSelect={false}
    />
  );
};
