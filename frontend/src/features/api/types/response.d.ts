type Response<TData> = TData;

interface PaginatedResponse<TItem> {
  count: number;
  next: number | null;
  previous: number | null;
  results: TItem[];
}

export type { PaginatedResponse, Response };
