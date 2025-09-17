import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { endpoints, useAuthAPI } from "../configs/API";
import { useNotification } from "../utils/toast";
import { statusLabels } from "./MyBookingPage";
import PaymentResultPopup from "../components/ui/PaymentResultPopup";
import {
    Calendar,
    Users,
    FileText,
    MessageSquare,
    ArrowLeft,
    FileDown,
    Star,
    StarHalf,
    XCircle
} from "lucide-react";
import { formatDate } from "../utils/formatDate";

const BookingDetailPage = () => {
    const { id } = useParams();
    const authAPI = useAuthAPI();
    const sendNotification = useNotification();
    const [booking, setBooking] = useState(null);
    const [feedbacks, setFeedbacks] = useState([]);
    const [loading, setLoading] = useState(true);

    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState("");
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        const loadBooking = async () => {
            try {
                const res = await authAPI.get(endpoints["my-booking"](id));
                setBooking(res.data);
            } catch (err) {
                console.error(err);
                sendNotification({ message: "Lỗi tải chi tiết đặt phòng!" }, "error");
            } finally {
                setLoading(false);
            }
        };
        const loadFeedbacks = async () => {
            try {
                const res = await authAPI.get(endpoints["my-feedbacks"](id));
                setFeedbacks(res.data);
            } catch (err) {
                console.error(err);
                sendNotification({ message: "Lỗi tải chi tiết phản hồi!" }, "error");
            }
        };

        loadBooking();
        loadFeedbacks();
    }, [id, authAPI, sendNotification]);

    const previewInvoice = async () => {
        try {
            const res = await authAPI.get(endpoints["export-invoice"](booking.id), {
                responseType: "blob",
            });
            const file = new Blob([res.data], { type: "application/pdf" });
            const fileURL = window.URL.createObjectURL(file);
            // download fileURL
            const link = document.createElement("a");
            link.href = fileURL;
            link.download = `invoice-${booking.id}.pdf`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        } catch (err) {
            console.error(err);
            sendNotification({ message: "Xem hóa đơn thất bại!" }, "error");
        }
    };

    const submitFeedback = async () => {
        if (!rating) {
            sendNotification({ message: "Vui lòng chọn số sao!" }, "warning");
            return;
        }
        setSubmitting(true);
        try {
            await authAPI.post(endpoints["my-feedbacks"](booking.id), {
                rating,
                comment,
            });
            sendNotification({ message: "Gửi đánh giá thành công!" }, "success");
            setRating(0);
            setComment("");
        } catch (err) {
            console.error(err);
            sendNotification({ message: "Gửi đánh giá thất bại!" }, "error");
        } finally {
            setSubmitting(false);
        }
    };

    const cancelBooking = async () => {
        if (!window.confirm("Bạn có chắc chắn muốn hủy đặt phòng này?")) return;
        try {
            await authAPI.put(endpoints["cancel-booking"](booking.id));
            sendNotification({ message: "Hủy đặt phòng thành công!" }, "success");
            setBooking({ ...booking, status: "CANCELLED" });
        } catch (err) {
            console.error(err);
            sendNotification({ message: "Hủy đặt phòng thất bại!" }, "error");
        }
    };

    const renderStars = (rating) => {
        const stars = [];
        for (let i = 1; i <= 5; i++) {
            if (rating >= i) {
                stars.push(
                    <Star key={i} size={12} className="text-yellow-400" fill="currentColor" />
                );
            } else if (rating >= i - 0.5) {
                stars.push(
                    <StarHalf key={i} size={15} className="text-yellow-400" fill="currentColor" />
                );
            } else {
                stars.push(<Star key={i} size={15} className="text-gray-300" />);
            }
        }
        return stars;
    };

    if (loading)
        return (
            <p className="p-6 text-center text-gray-500 dark:text-gray-300">
                Đang tải...
            </p>
        );
    if (!booking)
        return (
            <p className="p-6 text-center text-gray-500 dark:text-gray-300">
                Không tìm thấy đơn đặt phòng
            </p>
        );

    return (
        <div className="mx-auto max-w-7xl px-6 py-12 space-y-8">
            <div className="flex flex-col lg:flex-row lg:items-center md:justify-between gap-4">
                <div className="flex flex-row">
                    <h1 className="text-3xl font-bold dark:text-white me-5">
                        Đơn đặt phòng #{booking.code}
                    </h1>
                    <span
                        className={`inline-block h-full px-3 py-1 rounded-full text-white text-sm font-medium ${statusLabels[booking.status]?.color}`}
                    >
                        {statusLabels[booking.status]?.text}
                    </span>
                </div>

                <div className="flex items-center gap-3">
                    <button
                        onClick={previewInvoice}
                        className="inline-flex items-center gap-1 px-4 py-2 rounded-md bg-green-600 text-white hover:bg-green-700"
                    >
                        <FileDown size={16} /> Xuất hóa đơn
                    </button>
                    {booking.status !== "CHECKED_IN" && booking.status !== "CHECKED_OUT" && booking.status !== "CANCELLED" && (
                        <button
                            onClick={cancelBooking}
                            className="inline-flex items-center gap-1 px-4 py-2 rounded-md bg-red-600 text-white hover:bg-red-700"
                        >
                            <XCircle size={16} />Hủy phòng
                        </button>
                    )}
                    <Link
                        to="/my-bookings"
                        className="inline-flex items-center gap-1 px-4 py-2 rounded-md border border-indigo-600 text-indigo-600 hover:bg-indigo-50 dark:border-indigo-400 dark:text-indigo-400"
                    >
                        <ArrowLeft size={16} /> Quay lại
                    </Link>
                </div>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
                <div className="bg-white dark:bg-gray-800 shadow-lg rounded-2xl p-6 space-y-3">
                    <h2 className="flex items-center gap-2 text-lg font-semibold dark:text-gray-200">
                        <FileText size={18} /> Thông tin phòng
                    </h2>
                    <p className="text-gray-700 dark:text-gray-300">
                        Phòng{" "}
                        {booking.room?.roomType && (<>
                            <Link
                                to={`/rooms/${booking.room?.id}`}
                                className="text-indigo-600 hover:underline dark:text-indigo-400"
                            >
                                <span className="font-semibold">{booking.room?.roomNumber}</span> –{" "}
                                {booking.room?.roomType?.name}
                            </Link>
                        </>)}
                    </p>
                    {booking.room?.roomType && (
                        <p className="text-gray-700 dark:text-gray-300">
                            <span className="font-semibold">
                                {booking.room.roomType.pricePerNight.toLocaleString()}
                            </span>{" "}
                            VND/đêm
                        </p>
                    )}
                </div>
                <div className="bg-white dark:bg-gray-800 shadow-lg rounded-2xl p-6 space-y-3">
                    <h2 className="flex items-center gap-2 text-lg font-semibold dark:text-gray-200">
                        <Calendar size={18} /> Thời gian lưu trú
                    </h2>
                    <p className="text-gray-700 dark:text-gray-300">
                        Nhận phòng:{" "}
                        <span className="font-semibold">
                            {new Date(booking.checkInDate).toLocaleDateString("vi-VN")}
                        </span>
                    </p>
                    <p className="text-gray-700 dark:text-gray-300">
                        Trả phòng:{" "}
                        <span className="font-semibold">
                            {new Date(booking.checkOutDate).toLocaleDateString("vi-VN")}
                        </span>
                    </p>
                </div>
            </div>

            <div className="bg-white dark:bg-gray-800 shadow-lg rounded-2xl p-6 space-y-3">
                <h2 className="flex items-center gap-2 text-lg font-semibold dark:text-gray-200">
                    <Users size={18} /> Thông tin thêm
                </h2>
                <p className="text-gray-700 dark:text-gray-300">
                    Số khách: <span className="font-semibold">{booking.guests}</span>
                </p>
                {booking.specialRequest && (
                    <p className="text-gray-700 dark:text-gray-300">
                        Yêu cầu đặc biệt: <span className="italic">“{booking.specialRequest}”</span>
                    </p>
                )}
                <p className="text-gray-700 dark:text-gray-300">
                    Ngày đặt:{" "}
                    <span className="font-semibold">{formatDate(booking.createdAt)}</span>
                </p>
            </div>

            {/* Feedbacks */}
            <div className="bg-white dark:bg-gray-800 shadow-lg rounded-2xl p-6 space-y-4">
                <h2 className="flex items-center gap-2 text-lg font-semibold dark:text-gray-200">
                    <MessageSquare size={18} /> Đánh giá
                </h2>

                {feedbacks?.results?.length > 0 ? (
                    <ul className="space-y-3">
                        {feedbacks.results.map((f) => (
                            <li
                                key={f.id}
                                className="p-3 border rounded-xl dark:border-gray-700 dark:text-gray-200 bg-gray-50 dark:bg-gray-700/40"
                            >
                                <p className="mb-1">{f.comment || "Không có nội dung"}</p>
                                <div className="flex items-center gap-1">
                                    {renderStars(f.rating)}
                                    <span className="text-sm text-gray-400 ms-1">{f.rating}/5</span>
                                </div>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500 dark:text-gray-400">Chưa có đánh giá nào</p>
                )}

                <div className="pt-4 border-t dark:border-gray-700">
                    <h3 className="text-md font-semibold dark:text-gray-200 mb-2">
                        Viết đánh giá của bạn
                    </h3>
                    <div className="flex items-center gap-1 mb-3">
                        {[1, 2, 3, 4, 5].map((star) => {
                            const full = rating >= star;
                            const half = rating >= star - 0.5 && rating < star;

                            return (
                                <span
                                    key={star}
                                    className="relative cursor-pointer w-6 h-6"
                                >
                                    <span
                                        className="absolute left-0 top-0 w-1/2 h-full"
                                        onClick={() => setRating(star - 0.5)}
                                    ></span>
                                    <span
                                        className="absolute right-0 top-0 w-1/2 h-full"
                                        onClick={() => setRating(star)}
                                    ></span>

                                    {full ? (
                                        <Star size={20} className="text-yellow-400" fill="currentColor" />
                                    ) : half ? (
                                        <StarHalf size={20} className="text-yellow-400" fill="currentColor" />
                                    ) : (
                                        <Star size={20} className="text-gray-400" />
                                    )}
                                </span>
                            );
                        })}
                    </div>
                    <textarea
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        rows="3"
                        className="w-full rounded-lg border dark:border-gray-700 dark:bg-gray-900 dark:text-gray-200 p-2"
                        placeholder="Viết cảm nhận của bạn..."
                    />
                    <button
                        onClick={submitFeedback}
                        disabled={submitting}
                        className="mt-3 px-4 py-2 rounded-md bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-50"
                    >
                        {submitting ? "Đang gửi..." : "Gửi đánh giá"}
                    </button>
                </div>
            </div>

            <PaymentResultPopup />
        </div>
    );
};

export default BookingDetailPage;
