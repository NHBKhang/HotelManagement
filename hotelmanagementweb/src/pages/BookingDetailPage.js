import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { endpoints, useAuthAPI } from "../configs/API";
import { useNotification } from "../utils/toast";
import { statusLabels } from "./MyBookingPage";
import PaymentResultPopup from "../components/ui/PaymentResultPopup";

const BookingDetailPage = () => {
    const { id } = useParams();
    const authAPI = useAuthAPI();
    const sendNotification = useNotification();
    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadBooking = async () => {
            try {
                const res = await authAPI.get(`${endpoints["my-bookings"]}/${id}`);
                setBooking(res.data);
            } catch (err) {
                console.error(err);
                sendNotification({ message: "Lỗi tải chi tiết đặt phòng!" }, "error");
            } finally {
                setLoading(false);
            }
        };
        loadBooking();
    }, [id, authAPI, sendNotification]);

    if (loading) return <p className="p-6 text-center text-gray-500 dark:text-gray-300">Đang tải...</p>;
    if (!booking) return <p className="p-6 text-center text-gray-500 dark:text-gray-300">Không tìm thấy đơn đặt phòng</p>;

    return (
        <div className="max-w-4xl mx-auto px-6 py-12">
            <h1 className="text-3xl font-bold mb-6 dark:text-white">Chi tiết đặt phòng: {booking.code}</h1>

            <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-6 space-y-4">
                <div>
                    <h2 className="text-xl font-semibold dark:text-gray-200">Thông tin phòng</h2>
                    <p className="text-gray-700 dark:text-gray-300">
                        Phòng {booking.room?.roomNumber} - {booking.room?.roomType?.name}
                    </p>
                </div>

                <div>
                    <h2 className="text-xl font-semibold dark:text-gray-200">Ngày</h2>
                    <p className="text-gray-700 dark:text-gray-300">
                        Nhận phòng: {new Date(booking.checkInDate).toLocaleDateString("vi-VN")}
                    </p>
                    <p className="text-gray-700 dark:text-gray-300">
                        Trả phòng: {new Date(booking.checkOutDate).toLocaleDateString("vi-VN")}
                    </p>
                </div>

                <div>
                    <h2 className="text-xl font-semibold dark:text-gray-200">Thông tin khác</h2>
                    <p className="text-gray-700 dark:text-gray-300">Số khách: {booking.guests}</p>
                    <p className="text-gray-700 dark:text-gray-300">
                        Trạng thái:
                        <span className={`ml-2 px-2 py-1 rounded-full text-xs font-semibold ${statusLabels[booking.status]?.color}`}>
                            {statusLabels[booking.status]?.text}
                        </span>
                    </p>
                    {booking.specialRequest && (
                        <p className="text-gray-700 dark:text-gray-300">Yêu cầu đặc biệt: {booking.specialRequest}</p>
                    )}
                </div>

                {booking.feedbacks?.length > 0 && (
                    <div>
                        <h2 className="text-xl font-semibold dark:text-gray-200">Feedback</h2>
                        <ul className="space-y-2">
                            {booking.feedbacks.map((f) => (
                                <li key={f.id} className="p-2 border rounded-lg dark:border-gray-700 dark:text-gray-200">
                                    {f.comment || "Không có nội dung"} - {f.rating}/5
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                <div className="mt-4">
                    <Link to="/my-bookings" className="text-indigo-600 hover:underline dark:text-indigo-400">
                        ← Quay lại danh sách
                    </Link>
                </div>
            </div>
            <PaymentResultPopup />
        </div>
    );
};

export default BookingDetailPage;
