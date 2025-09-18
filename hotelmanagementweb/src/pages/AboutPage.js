import { motion } from "framer-motion";
import { Hotel, CreditCard, Gift } from "lucide-react";

const AboutPage = () => {
    const features = [
        {
            title: "Đa dạng lựa chọn",
            desc: "Hơn 100+ khách sạn, từ bình dân đến cao cấp, phù hợp cho gia đình, cặp đôi hoặc chuyến công tác.",
            icon: <Hotel className="w-10 h-10 text-indigo-500" />,
        },
        {
            title: "Thanh toán linh hoạt",
            desc: "Hỗ trợ nhiều hình thức thanh toán: thẻ tín dụng, ví điện tử, chuyển khoản và thanh toán tại quầy.",
            icon: <CreditCard className="w-10 h-10 text-indigo-500" />,
        },
        {
            title: "Ưu đãi & khuyến mãi",
            desc: "Luôn có chương trình giảm giá đặc biệt cho khách hàng thân thiết và đặt phòng sớm.",
            icon: <Gift className="w-10 h-10 text-indigo-500" />,
        },
    ];

    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-cyan-50 dark:from-gray-900 dark:via-gray-950 dark:to-gray-900 flex items-center justify-center px-6 py-12 transition-colors duration-300">
            <div className="max-w-5xl w-full text-center space-y-12">
                <motion.h1
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.6 }}
                    className="text-4xl md:text-5xl font-extrabold text-gray-900 dark:text-white"
                >
                    Hệ thống đặt phòng khách sạn thông minh & tiện lợi
                </motion.h1>

                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.2, duration: 0.6 }}
                    className="space-y-4 max-w-3xl mx-auto text-gray-700 dark:text-gray-300"
                >
                    <p>
                        Chúng tôi mang đến giải pháp đặt phòng khách sạn toàn diện, cho phép bạn tìm kiếm, so sánh giá và đặt phòng chỉ trong vài bước đơn giản.
                        Với hệ thống quản lý thông minh, bạn sẽ luôn nắm rõ tình trạng phòng trống, giá ưu đãi và nhận được xác nhận ngay lập tức.
                    </p>
                    <p>
                        Không chỉ dừng lại ở việc đặt phòng, chúng tôi còn cung cấp gợi ý các tiện ích như dịch vụ spa, phòng gym, và các gói nghỉ dưỡng phù hợp với nhu cầu cá nhân.
                        Tất cả nhằm mang lại trải nghiệm thoải mái, tiện nghi nhất cho khách hàng.
                    </p>
                </motion.div>

                {/* Feature Section */}
                <div className="grid md:grid-cols-3 gap-6 mt-10">
                    {features.map((item, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.3 + i * 0.1 }}
                            className="bg-white dark:bg-gray-800 shadow-md rounded-2xl p-6 flex flex-col items-center text-center border border-gray-200 dark:border-gray-700"
                        >
                            <div className="mb-3">{item.icon}</div>
                            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                                {item.title}
                            </h3>
                            <p className="text-gray-600 dark:text-gray-400 text-sm mt-2">
                                {item.desc}
                            </p>
                        </motion.div>
                    ))}
                </div>

                {/* CTA */}
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: 0.5 }}
                    className="mt-12"
                >
                    <a
                        href="/search"
                        className="px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white text-lg font-semibold rounded-full shadow-lg transition-colors"
                    >
                        Bắt đầu đặt phòng ngay
                    </a>
                </motion.div>
            </div>
        </div>
    );
};

export default AboutPage;
