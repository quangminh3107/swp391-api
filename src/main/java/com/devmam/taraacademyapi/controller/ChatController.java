// package com.devmam.taraacademyapi.controller;

// import com.devmam.taraacademyapi.models.dto.response.MessageDto;
// import com.devmam.taraacademyapi.models.entities.User;
// import com.devmam.taraacademyapi.service.JwtService;
// import com.devmam.taraacademyapi.service.impl.entities.ChatService;
// import com.devmam.taraacademyapi.service.impl.entities.UserService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.handler.annotation.DestinationVariable;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
// import org.springframework.stereotype.Controller;

// import java.util.Optional;
// import java.util.UUID;

// @Controller
// public class ChatController {

//     @Autowired
//     private ChatService chatService;

//     @Autowired
//     private JwtService jwtService;

//     @Autowired
//     private UserService userService;

//     @MessageMapping("/chat/{chatId}")
//     @SendTo("/topic/chat/{chatId}")
//     public MessageDto sendMessage(
//             @DestinationVariable Integer chatId,
//             MessageDto message,
//             SimpMessageHeaderAccessor headerAccessor) {

//         // Lấy token từ header của WebSocket message
//         String token = extractTokenFromHeaders(headerAccessor);
//         UUID userId = null;

//         if (token != null && !token.isEmpty()) {
//             try {
//                 // User đã đăng nhập
//                 userId = jwtService.getUserId(token);
//                 Optional<User> userOpt = userService.getOne(userId);

//                 if (userOpt.isPresent()) {
//                     User user = userOpt.get();
//                     message.setSendBy(user.getId().toString());

//                     // Check role để xác định isFromUser
//                     // ROLE_STUDENT = customer (true)
//                     // ROLE_TEACHER/ADMIN = support (false)
//                     boolean isCustomer = "ROLE_STUDENT".equals(user.getRole());
//                     message.setIsFromUser(isCustomer);

//                 } else {
//                     // User không tồn tại, xử lý như guest
//                     message.setSendBy("guest");
//                     message.setIsFromUser(true);
//                     userId = null;
//                 }
//             } catch (Exception e) {
//                 // Token không hợp lệ, xử lý như guest
//                 message.setSendBy("guest");
//                 message.setIsFromUser(true);
//             }
//         } else {
//             // Không có token - anonymous user
//             message.setSendBy("anonymous");
//             message.setIsFromUser(true);
//         }

//         return chatService.saveAndBroadcast(chatId, message, userId, !message.getIsFromUser());
//     }

//     @MessageMapping("/chat/{chatId}/join")
//     public void joinChat(
//             @DestinationVariable Integer chatId,
//             SimpMessageHeaderAccessor headerAccessor) {

//         String token = extractTokenFromHeaders(headerAccessor);
//         User user = null;

//         if (token != null && !token.isEmpty()) {
//             try {
//                 UUID userId = jwtService.getUserId(token);
//                 Optional<User> userOpt = userService.getOne(userId);
//                 user = userOpt.orElse(null);
//             } catch (Exception e) {
//                 // Token không hợp lệ
//                 user = null;
//             }
//         }

//         chatService.notifyUserJoined(chatId, user);
//     }

//     /**
//      * Extract JWT token from WebSocket message headers
//      * Token có thể được gửi qua header "Authorization" hoặc "token"
//      */
//     private String extractTokenFromHeaders(SimpMessageHeaderAccessor headerAccessor) {
//         // Thử lấy từ Authorization header
//         String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             return authHeader.substring(7);
//         }

//         // Thử lấy từ token header (nếu client gửi trực tiếp)
//         String token = headerAccessor.getFirstNativeHeader("token");
//         if (token != null && !token.isEmpty()) {
//             return token;
//         }

//         return null;
//     }
// }