import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { endpoints, useAuthAPI } from "../configs/API";
import { useNotification } from "../utils/toast";

export const statusLabels = {
    PENDING: {
        text: "Chờ xác nhận",
        color: "bg-yellow-100 text-yellow-800 dark:bg-yellow-700 dark:text-yellow-100"
    },
    PROCESSING: {
        text: "Đang xử lý",
        color: "bg-blue-100 text-blue-800 dark:bg-blue-700 dark:text-blue-100"
    },
    CONFIRMED: {
        text: "Đã xác nhận",
        color: "bg-teal-100 text-teal-800 dark:bg-teal-700 dark:text-teal-100"
    },
    CHECKED_IN: {
        text: "Đang ở",
        color: "bg-green-100 text-green-800 dark:bg-green-700 dark:text-green-100"
    },
    CHECKED_OUT: {
        text: "Đã trả phòng",
        color: "bg-indigo-100 text-indigo-800 dark:bg-indigo-700 dark:text-indigo-100"
    },
    CANCELLED: {
        text: "Đã hủy",
        color: "bg-red-100 text-red-800 dark:bg-red-700 dark:text-red-100"
    },
};

const MyBookingPage = () => {
    const [data, setData] = useState({ results: [], totalPages: 1 });
    const [loading, setLoading] = useState(true);
    const [status, setStatus] = useState("");
    const [page, setPage] = useState(1);
    const authAPI = useAuthAPI();
    const sendNotification = useNotification();

    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            try {
                const res = await authAPI.get(
                    `${endpoints["my-bookings"]}?status=${status}&page=${page}`
                );
                setData(res.data);
            } catch (err) {
                console.error(err);
                sendNotification({ message: "Lỗi tải danh sách đặt phòng!" }, "error");
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, [status, page, authAPI, sendNotification]);

    if (loading)
        return (
            <p className="p-6 text-center text-gray-500 dark:text-gray-400 animate-pulse text-lg">
                Đang tải danh sách...
            </p>
        );

    return (
        <div className="max-w-7xl mx-auto py-6 px-5">
            <h1 className="text-3xl font-bold mb-6 text-gray-800 dark:text-gray-100">
                Danh sách đơn đặt phòng
            </h1>

            <div className="mb-6 flex gap-4">
                <select
                    value={status}
                    onChange={(e) => {
                        setStatus(e.target.value);
                        setPage(1);
                    }}
                    className="border rounded-lg px-3 py-2 
                     focus:ring focus:ring-indigo-300 
                     bg-white text-gray-800 text-base
                     dark:bg-gray-800 dark:text-gray-200 dark:border-gray-600"
                >
                    <option value="">-- Tất cả trạng thái --</option>
                    {Object.entries(statusLabels).map(([key, { text }]) => (
                        <option key={key} value={key}>
                            {text}
                        </option>
                    ))}
                </select>
            </div>

            <div className="overflow-x-auto bg-white dark:bg-gray-900 shadow rounded-lg">
                <table className="w-full border-collapse">
                    <thead>
                        <tr className="bg-gray-100 dark:bg-gray-800 text-left text-base font-semibold text-gray-700 dark:text-gray-300">
                            <th className="px-4 py-3 border dark:border-gray-700">Mã</th>
                            <th className="px-4 py-3 border dark:border-gray-700">Phòng</th>
                            <th className="px-4 py-3 border dark:border-gray-700">Nhận phòng</th>
                            <th className="px-4 py-3 border dark:border-gray-700">Trả phòng</th>
                            <th className="px-4 py-3 border dark:border-gray-700">Trạng thái</th>
                            <th className="px-4 py-3 border dark:border-gray-700">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.results?.length > 0 ? (
                            data.results.map((b) => {
                                const badge = statusLabels[b.status] || { text: b.status, color: "bg-gray-200" };
                                return (
                                    <tr
                                        key={b.id}
                                        className="text-base hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
                                    >
                                        <td className="px-4 py-2 border dark:border-gray-700">{b.code}</td>
                                        <td className="px-4 py-2 border dark:border-gray-700">
                                            Phòng {b.room?.roomNumber} - {b.room?.roomType?.name}
                                        </td>
                                        <td className="px-4 py-2 border dark:border-gray-700">
                                            {new Date(b.checkInDate).toLocaleDateString("vi-VN")}
                                        </td>
                                        <td className="px-4 py-2 border dark:border-gray-700">
                                            {new Date(b.checkOutDate).toLocaleDateString("vi-VN")}
                                        </td>
                                        <td className="px-4 py-2 border dark:border-gray-700">
                                            <span className={`px-3 py-1 rounded-full text-sm font-semibold ${badge.color}`}>
                                                {badge.text}
                                            </span>
                                        </td>
                                        <td className="px-4 py-2 border dark:border-gray-700">
                                            <Link
                                                to={`/my-bookings/${b.id}`}
                                                className="text-indigo-600 dark:text-indigo-400 hover:underline"
                                            >
                                                Xem chi tiết
                                            </Link>
                                        </td>
                                    </tr>
                                );
                            })
                        ) : (
                            <tr>
                                <td
                                    colSpan="6"
                                    className="px-4 py-6 text-center text-gray-500 dark:text-gray-400 text-lg"
                                >
                                    Chưa có đơn đặt phòng nào.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {data.total > 1 && (
                <div className="flex justify-center items-center gap-3 mt-6">
                    <button
                        onClick={() => setPage((p) => Math.max(1, p - 1))}
                        disabled={page === 1}
                        className="px-3 py-1 rounded-md border text-base
                       bg-gray-100 dark:bg-gray-800
                       text-gray-700 dark:text-gray-300
                       disabled:opacity-50"
                    >
                        ← Trước
                    </button>
                    <span className="text-gray-700 dark:text-gray-300 text-base">
                        Trang {data.current} / {data.total}
                    </span>
                    <button
                        onClick={() => setPage((p) => Math.min(data.total, p + 1))}
                        disabled={page === data.total}
                        className="px-3 py-1 rounded-md border text-base
                       bg-gray-100 dark:bg-gray-800
                       text-gray-700 dark:text-gray-300
                       disabled:opacity-50"
                    >
                        Sau →
                    </button>
                </div>
            )}
        </div>
    );
};

export default MyBookingPage;
