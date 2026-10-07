import React from "react";

export interface MasterDetailLayoutProps {
  queueTitle: string;
  queueCount?: number;
  queueFilters?: React.ReactNode;
  queueContent: React.ReactNode;
  detailContent: React.ReactNode;
  topHeader?: React.ReactNode;
}

export function MasterDetailLayout({
  queueTitle,
  queueCount,
  queueFilters,
  queueContent,
  detailContent,
  topHeader,
}: MasterDetailLayoutProps) {
  return (
    <div className="flex flex-col h-[calc(100vh-4rem)] w-full overflow-hidden bg-slate-50/75">
      {topHeader && (
        <div className="shrink-0 border-b border-slate-200/80 bg-white px-6 py-3">
          {topHeader}
        </div>
      )}

      <div className="flex flex-1 w-full overflow-hidden">
        {/* Left Queue Pane (Fixed 380px–420px, overflow-y-auto) */}
        <aside className="w-[380px] lg:w-[420px] flex-shrink-0 flex flex-col border-r border-slate-200/80 bg-white">
          <div className="p-4 border-b border-slate-100 flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <h2 className="text-sm font-semibold text-slate-900 tracking-tight">
                {queueTitle}
              </h2>
              {queueCount !== undefined && (
                <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-slate-100 text-slate-600 border border-slate-200/60">
                  {queueCount}
                </span>
              )}
            </div>
            {queueFilters}
          </div>
          <div className="flex-1 overflow-y-auto divide-y divide-slate-100">
            {queueContent}
          </div>
        </aside>

        {/* Right Inspection Desk (flex-1, overflow-y-auto) */}
        <main className="flex-1 flex flex-col overflow-y-auto bg-slate-50/75 p-6">
          <div className="w-full max-w-5xl mx-auto flex-1 flex flex-col">
            {detailContent}
          </div>
        </main>
      </div>
    </div>
  );
}
