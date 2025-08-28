const Pagination = ({ current, total, onPageChange }) => {
    const MAX_VISIBLE = 5;
    let start = Math.max(1, current - Math.floor(MAX_VISIBLE / 2));
    let end = start + MAX_VISIBLE - 1;

    if (end > total) {
        end = total;
        start = Math.max(1, end - MAX_VISIBLE + 1);
    }

    const pages = Array.from({ length: end - start + 1 }, (_, i) => start + i);

    return (
        <div className="flex items-center justify-center gap-2 mt-6">
            <button
                onClick={() => onPageChange(current - 1)}
                disabled={current === 1}
                className="px-3 py-2 rounded-lg border bg-white dark:bg-slate-900 disabled:opacity-50 hover:bg-slate-100 dark:hover:bg-slate-800 transition"
            >
                <i className="fa fa-chevron-left"></i>
            </button>

            {start > 1 && (
                <>
                    <button
                        onClick={() => onPageChange(1)}
                        className="w-10 h-10 flex items-center justify-center rounded-lg border bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800 transition"
                    >
                        1
                    </button>
                    {start > 2 && <span className="px-2">...</span>}
                </>
            )}

            {pages.map((p) => (
                <button
                    key={p}
                    onClick={() => onPageChange(p)}
                    className={`w-10 h-10 flex items-center justify-center rounded-lg border transition ${current === p
                            ? "bg-slate-900 text-white dark:bg-white dark:text-slate-900"
                            : "bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800"
                        }`}
                >
                    {p}
                </button>
            ))}

            {end < total && (
                <>
                    {end < total - 1 && <span className="px-2">...</span>}
                    <button
                        onClick={() => onPageChange(total)}
                        className="w-10 h-10 flex items-center justify-center rounded-lg border bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800 transition"
                    >
                        {total}
                    </button>
                </>
            )}

            <button
                onClick={() => onPageChange(current + 1)}
                disabled={current === total}
                className="px-3 py-2 rounded-lg border bg-white dark:bg-slate-900 disabled:opacity-50 hover:bg-slate-100 dark:hover:bg-slate-800 transition"
            >
                <i className="fa fa-chevron-right"></i>
            </button>
        </div>
    );
};

export default Pagination;
