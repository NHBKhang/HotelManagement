import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import API, { endpoints } from "../configs/API";

const RoomPage = () => {
    const { id } = useParams();
    const [room, setRoom] = useState(null);
    const [payload, setPayload] = useState({});
    const [loading, setLoading] = useState(true);

    const updatePayload = (e) =>
        setPayload((p) => ({ ...p, [e.target.name]: e.target.value }));

    useEffect(() => {
        const fetchRoom = async () => {
            try {
                const res = await API.get(endpoints.room(id));
                setRoom(res.data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchRoom();
    }, [id]);

    if (loading) return <p className="p-6 text-center text-gray-500">Đang tải...</p>;
    if (!room) return <p className="p-6 text-center text-red-500">Không tìm thấy phòng.</p>;

    const roomType = room.roomType || {};
    const today = new Date().toISOString().split("T")[0];
    const minCheckout = payload.checkin || today;

    return (
        <div className="max-w-7xl mx-auto px-6 py-12 space-y-10">
            <div className="relative w-full h-80 md:h-96 rounded-xl overflow-hidden shadow-lg">
                <img
                    src={room.image || "/img/room-placeholder.jpg"}
                    alt={roomType.name || room.roomNumber}
                    className="w-full h-full object-cover"
                />
                <div className="absolute bottom-0 left-0 w-full bg-gradient-to-t from-black/70 to-transparent p-6 text-white">
                    <h1 className="text-3xl font-bold">Phòng {room.roomNumber} - {roomType.name}</h1>
                    <p className="mt-1 text-sm">Sức chứa tối đa: {roomType.maxGuests || 1} khách</p>
                    <p className="mt-1 text-sm">Diện tích: {room.size || "N/A"}</p>
                    <span className={`inline-block mt-2 px-3 py-1 text-sm font-medium rounded-full ${room.tailwindClass || "bg-gray-200 text-gray-800"}`}>
                        {room.statusDescription || room.status}
                    </span>
                </div>
            </div>

            <div className="grid md:grid-cols-3 gap-10">
                <div className="md:col-span-2 space-y-8">
                    <section className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-3">
                        <h2 className="text-2xl font-semibold border-b pb-2">Mô tả</h2>
                        <p className="text-slate-700 dark:text-slate-300">{roomType.description || "Phòng thoải mái, đầy đủ tiện nghi."}</p>
                    </section>

                    <section className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-3">
                        <h2 className="text-2xl font-semibold border-b pb-2">Tiện nghi</h2>
                        <ul className="list-disc list-inside text-slate-700 dark:text-slate-300 space-y-1">
                            {roomType.amenities?.length > 0 ? (
                                roomType.amenities.map((a, idx) => <li key={idx}>{a}</li>)
                            ) : (
                                <li>Chưa có thông tin tiện nghi.</li>
                            )}
                        </ul>
                    </section>

                    <section className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-3">
                        <h2 className="text-2xl font-semibold border-b pb-2">Chính sách</h2>
                        <p className="text-slate-700 dark:text-slate-300">{roomType.policy || "Hủy miễn phí trước 24h."}</p>
                    </section>
                </div>

                <div className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow space-y-6">
                    <div className="text-center">
                        <span className="text-4xl font-bold text-indigo-600">
                            {roomType.pricePerNight?.toLocaleString()}₫
                        </span>
                        <p className="text-sm text-gray-500">/ đêm</p>
                    </div>
                    <form className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium mb-1">Nhận phòng</label>
                            <input
                                name="checkin"
                                onChange={updatePayload}
                                value={payload.checkin}
                                type="date"
                                min={today}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-400 focus:outline-none dark:text-black" />
                        </div>
                        <div>
                            <label className="block text-sm font-medium mb-1">Trả phòng</label>
                            <input
                                name="checkout"
                                onChange={updatePayload}
                                value={payload.checkout}
                                type="date"
                                min={minCheckout}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-400 focus:outline-none dark:text-black" />
                        </div>
                        <div>
                            <label className="block text-sm font-medium mb-1">Số khách</label>
                            <input
                                type="number"
                                min="1"
                                max={roomType.maxGuests || 10}
                                step="1"
                                defaultValue="1"
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-400 focus:outline-none dark:text-black" />
                        </div>
                        <div className="my-4 h-px bg-slate-200 dark:bg-slate-800" />
                        <button
                            type="submit"
                            className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors duration-200"
                        >
                            Đặt phòng
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default RoomPage;
