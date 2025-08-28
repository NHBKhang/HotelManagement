import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import API, { endpoints } from "../configs/API";

const RoomPage = () => {
    const { id } = useParams();
    const [room, setRoom] = useState(null);
    const [loading, setLoading] = useState(true);

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

    if (loading) return <p className="p-6">Đang tải...</p>;
    if (!room) return <p className="p-6">Không tìm thấy phòng.</p>;

    return (
        <div className="max-w-7xl mx-auto px-6 py-12">
            <div className="relative w-full h-72 md:h-96 mb-8">
                <img
                    src={room.image || "/img/room-placeholder.jpg"}
                    alt={room.name}
                    className="w-full h-full object-cover rounded-xl shadow"
                />
                <div className="absolute bottom-4 left-6 bg-white/80 dark:bg-slate-900/80 px-4 py-2 rounded-lg shadow">
                    <h1 className="text-2xl md:text-3xl font-bold">{room.name}</h1>
                    <p className="text-slate-600 dark:text-slate-300">
                        {room.type} · {room.size || "Diện tích: N/A"}
                    </p>
                </div>
            </div>

            <div className="grid md:grid-cols-3 gap-10">
                <div className="md:col-span-2 space-y-6">
                    <h2 className="text-xl font-semibold">Mô tả</h2>
                    <p className="text-slate-600 dark:text-slate-300">
                        {room.description || "Phòng thoải mái, đầy đủ tiện nghi."}
                    </p>

                    <h2 className="text-xl font-semibold">Tiện nghi</h2>
                    <ul className="list-disc list-inside text-slate-600 dark:text-slate-300">
                        {room.amenities?.length > 0 ? (
                            room.amenities.map((a, idx) => <li key={idx}>{a}</li>)
                        ) : (
                            <li>Chưa có thông tin tiện nghi.</li>
                        )}
                    </ul>

                    <h2 className="text-xl font-semibold">Chính sách</h2>
                    <p className="text-slate-600 dark:text-slate-300">
                        {room.policy || "Hủy miễn phí trước 24h."}
                    </p>
                </div>

                <div className="p-6 rounded-xl border bg-white dark:bg-slate-900 shadow space-y-4">
                    <div>
                        <span className="text-3xl font-bold text-indigo-600">
                            {room.price?.toLocaleString()}₫
                        </span>{" "}
                        / đêm
                    </div>
                    <form className="space-y-4">
                        <div>
                            <label className="text-sm">Nhận phòng</label>
                            <input type="date" className="w-full border rounded-lg px-3 py-2" />
                        </div>
                        <div>
                            <label className="text-sm">Trả phòng</label>
                            <input type="date" className="w-full border rounded-lg px-3 py-2" />
                        </div>
                        <div>
                            <label className="text-sm">Số khách</label>
                            <input
                                type="number"
                                min="1"
                                defaultValue="1"
                                className="w-full border rounded-lg px-3 py-2"
                            />
                        </div>
                        <button
                            type="submit"
                            className="w-full bg-indigo-600 text-white py-3 rounded-lg hover:bg-indigo-700"
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
