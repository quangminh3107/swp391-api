// package com.devmam.taraacademyapi.controller;

// import com.devmam.taraacademyapi.models.dto.response.ChatDto;
// import com.devmam.taraacademyapi.models.dto.response.MessageDto;
// import com.devmam.taraacademyapi.models.dto.response.ResponseData;
// import com.devmam.taraacademyapi.service.JwtService;
// import com.devmam.taraacademyapi.service.impl.entities.ChatService;
// import com.devmam.taraacademyapi.worker.ChatBotWorker;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/v1/chats")
// public class ChatRestController {

//     @Autowired
//     private ChatService chatService;

//     @Autowired
//     private JwtService jwtService;

//     @Autowired
//     private ChatBotWorker chatBotWorker;

//     /**
//      * Tạo chat mới - CHO PHÉP ANONYMOUS (không cần đăng nhập)
//      * FE sẽ lưu chatId vào cookies
//      */
//     @PostMapping
//     public ResponseEntity<ResponseData<ChatDto>> createChat(
//             @RequestHeader(value = "Authorization", required = false) String authHeader) {
//         try {
//             ChatDto chatDto;

//             // Kiểm tra user có đăng nhập không
//             if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                 // User đã đăng nhập - tạo chat có liên kết với userId
//                 String token = jwtService.getTokenFromAuthHeader(authHeader);
//                 UUID userId = jwtService.getUserId(token);
//                 chatDto = chatService.createChatForUser(userId);
//             } else {
//                 // User chưa đăng nhập - tạo anonymous chat
//                 chatDto = chatService.createAnonymousChat();
//             }

//             return ResponseEntity.ok(
//                     ResponseData.<ChatDto>builder()
//                             .status(200)
//                             .message("Chat created successfully")
//                             .data(chatDto)
//                             .build()
//             );
//         } catch (Exception e) {
//             return ResponseEntity.status(500)
//                     .body(ResponseData.<ChatDto>builder()
//                             .status(500)
//                             .message("Failed to create chat")
//                             .error(e.getMessage())
//                             .build()
//                     );
//         }
//     }

//     /**
//      * Lấy chat theo ID - CHO PHÉP ANONYMOUS
//      * Dùng cho user chưa đăng nhập (lấy từ cookies)
//      */
//     @GetMapping("/{chatId}")
//     public ResponseEntity<ResponseData<ChatDto>> getChatById(
//             @PathVariable Integer chatId,
//             @RequestHeader(value = "Authorization", required = false) String authHeader) {
//         try {
//             ChatDto chatDto;

//             if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                 // User đã đăng nhập - verify ownership
//                 String token = jwtService.getTokenFromAuthHeader(authHeader);
//                 UUID userId = jwtService.getUserId(token);
//                 chatDto = chatService.getChatByIdForUser(chatId, userId);
//             } else {
//                 // Anonymous user - chỉ lấy chat
//                 chatDto = chatService.getChatByIdAnonymous(chatId);
//             }

//             return ResponseEntity.ok(
//                     ResponseData.<ChatDto>builder()
//                             .status(200)
//                             .message("Success")
//                             .data(chatDto)
//                             .build()
//             );
//         } catch (Exception e) {
//             return ResponseEntity.status(404)
//                     .body(ResponseData.<ChatDto>builder()
//                             .status(404)
//                             .message("Chat not found")
//                             .error(e.getMessage())
//                             .build()
//                     );
//         }
//     }

//     /**
//      * Lấy lịch sử tin nhắn - CHO PHÉP ANONYMOUS
//      */
//     @GetMapping("/{chatId}/messages")
//     public ResponseEntity<ResponseData<List<MessageDto>>> getChatHistory(
//             @PathVariable Integer chatId,
//             @RequestHeader(value = "Authorization", required = false) String authHeader) {
//         try {
//             List<MessageDto> messages;

//             if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                 // User đã đăng nhập
//                 String token = jwtService.getTokenFromAuthHeader(authHeader);
//                 UUID userId = jwtService.getUserId(token);
//                 messages = chatService.getChatHistoryForUser(chatId, userId);
//             } else {
//                 // Anonymous user
//                 messages = chatService.getChatHistoryAnonymous(chatId);
//             }

//             return ResponseEntity.ok(
//                     ResponseData.<List<MessageDto>>builder()
//                             .status(200)
//                             .message("Success")
//                             .data(messages)
//                             .build()
//             );
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(404)
//                     .body(ResponseData.<List<MessageDto>>builder()
//                             .status(404)
//                             .message("Failed to get chat history")
//                             .error(e.getMessage())
//                             .build()
//                     );
//         }
//     }

//     /**
//      * Lấy danh sách tất cả chats của user đã đăng nhập
//      */
//     @GetMapping("/my-chats")
//     @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<ResponseData<List<ChatDto>>> getMyChats(
//             @RequestHeader("Authorization") String authHeader) {
//         try {
//             String token = jwtService.getTokenFromAuthHeader(authHeader);
//             UUID userId = jwtService.getUserId(token);

//             List<ChatDto> chats = chatService.getUserChats(userId);

//             return ResponseEntity.ok(
//                     ResponseData.<List<ChatDto>>builder()
//                             .status(200)
//                             .message("Success")
//                             .data(chats)
//                             .build()
//             );
//         } catch (Exception e) {
//             return ResponseEntity.status(500)
//                     .body(ResponseData.<List<ChatDto>>builder()
//                             .status(500)
//                             .message("Failed to get chats")
//                             .error(e.getMessage())
//                             .build()
//                     );
//         }
//     }

//     /**
//      * Xóa chat - CHỈ CHO USER ĐÃ ĐĂNG NHẬP
//      */
//     @DeleteMapping("/{chatId}")
//     @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<ResponseData<Void>> deleteChat(
//             @PathVariable Integer chatId,
//             @RequestHeader("Authorization") String authHeader) {
//         try {
//             String token = jwtService.getTokenFromAuthHeader(authHeader);
//             UUID userId = jwtService.getUserId(token);

//             chatService.deleteChat(chatId, userId);

//             return ResponseEntity.ok(
//                     ResponseData.<Void>builder()
//                             .status(200)
//                             .message("Chat deleted successfully")
//                             .build()
//             );
//         } catch (Exception e) {
//             return ResponseEntity.status(404)
//                     .body(ResponseData.<Void>builder()
//                             .status(404)
//                             .message("Failed to delete chat")
//                             .error(e.getMessage())
//                             .build()
//                     );
//         }
//     }

//     /**
//      * Lấy tất cả chats (dành cho admin/support staff)
//      */
//     @GetMapping("/all")
//     @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
//     public ResponseEntity<ResponseData<List<ChatDto>>> getAllChats(
//             @RequestParam(required = false, defaultValue = "false") Boolean anonymousOnly) {
//         try {
//             List<ChatDto> chats;

//             if (anonymousOnly) {
//                 chats = chatService.getAllAnonymousChats();
//             } else {
//                 chats = chatService.getAllChats();
//             }

//             return ResponseEntity.ok(
//                     ResponseData.<List<ChatDto>>builder()
//                             .status(200)
//                             .message("Success")
//                             .data(chats)
//                             .build()
//             );
//         } catch (Exception e) {
//             return ResponseEntity.status(500)
//                     .body(ResponseData.<List<ChatDto>>builder()
//                             .status(500)
//                             .message("Failed to get all chats")
//                             .error(e.getMessage())
//                             .build()
//                     );
//         }
//     }

//     /**
//      * Link anonymous chat với user account (khi user đăng nhập sau)
//      */
//     @PostMapping("/{chatId}/link-to-user")
//     @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<ResponseData<ChatDto>> linkChatToUser(
//             @PathVariable Integer chatId,
//             @RequestHeader("Authorization") String authHeader) {
//         try {
//             String token = jwtService.getTokenFromAuthHeader(authHeader);
//             UUID userId = jwtService.getUserId(token);

//             ChatDto chatDto = chatService.linkAnonymousChatToUser(chatId, userId);

//             return ResponseEntity.ok(
//                     ResponseData.<ChatDto>builder()
//                             .status(200)
//                             .message("Chat linked to user successfully")
//                             .data(chatDto)
//                             .build()
//             );
//         } catch (Exception e) {
//             return ResponseEntity.status(400)
//                     .body(ResponseData.<ChatDto>builder()
//                             .status(400)
//                             .message("Failed to link chat to user")
//                             .error(e.getMessage())
//                             .build()
//                     );
//         }
//     }

//     //Tắt bật chatbot
//     @PostMapping("/chat-bot/on")
//     public ResponseEntity<ResponseData<String>> chatBotOn(){
//         chatBotWorker.setIsRunning(true);

//         System.out.println(chatBotWorker.getIsRunning());

//         return ResponseEntity.ok(
//           ResponseData.<String>builder()
//                   .status(200)
//                   .message("Chatbot đang bật")
//                   .data("Chatbot đang bật")
//                   .error(null)
//                   .build()
//         );
//     }

//     @PostMapping("/chat-bot/off")
//     public ResponseEntity<ResponseData<String>> chatBotOff(){
//         chatBotWorker.setIsRunning(false);

//         System.out.println(chatBotWorker.getIsRunning());

//         return ResponseEntity.ok(
//                 ResponseData.<String>builder()
//                         .status(200)
//                         .message("Chatbot đang tắt")
//                         .data("Chatbot đang tắt")
//                         .error(null)
//                         .build()
//         );
//     }

//     @GetMapping("/chat-bot/status")
//     public ResponseEntity<ResponseData<Boolean>> chatBotStatus(){
//         return ResponseEntity.ok(
//                 ResponseData.<Boolean>builder()
//                         .status(200)
//                         .message("Chatbot đang "+(chatBotWorker.getIsRunning()? "Bật":"Tắt"))
//                         .data(chatBotWorker.getIsRunning())
//                         .error(null)
//                         .build()
//         );
//     }
// }