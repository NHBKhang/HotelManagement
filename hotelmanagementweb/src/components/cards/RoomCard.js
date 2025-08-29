import { useNavigate } from "react-router-dom";

const RoomCard = ({ room }) => {
    const navigate = useNavigate();

    return (
        <div className="border rounded-2xl p-4 bg-white shadow-lg hover:shadow-xl transition duration-300 
                        dark:bg-gray-200 dark:border-gray-100">
            <img
                src={room.image || "/img/room-placeholder.jpg"}
                alt={`Phòng ${room.roomNumber}`}
                className="w-full h-40 object-cover rounded-xl mb-3"
            />

            <h3 className="text-lg font-semibold text-gray-800 mb-1 dark:text-gray-700">
                Phòng {room.roomNumber}
            </h3>

            <p className="text-sm text-indigo-600 font-medium dark:text-indigo-400">
                {room.roomType?.name || "Chưa gán loại"}
            </p>

            <p className="text-sm text-slate-500 mt-1 line-clamp-2 dark:text-slate-600">
                {room.roomType?.description || "Không có mô tả"}
            </p>

            <div className="flex items-center justify-between mt-4">
                <div>
                    <div className="text-lg font-bold text-emerald-600 dark:text-emerald-500">
                        {room.roomType?.pricePerNight?.toLocaleString() || 0} VNĐ/đêm
                    </div>
                    <div
                        className={`inline-block px-3 py-1 rounded-full text-xs font-semibold 
                                   ${room.tailwindClass} dark:opacity-90`}
                    >
                        {room.statusDescription}
                    </div>
                </div>

                <button
                    onClick={() => navigate(`/rooms/${room.id}`)}
                    className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg shadow-sm transition 
                               dark:bg-indigo-500 dark:hover:bg-indigo-600"
                >
                    Chi tiết
                </button>
            </div>
        </div>
    );
};

export default RoomCard;
