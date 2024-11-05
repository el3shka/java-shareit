INSERT INTO public.users (name, email)
VALUES ('User1', 'email1@email.com'),
       ('User2', 'email2@email.com'),
       ('User3', 'email3@email.com'),
       ('User4', 'email4@email.com'),
       ('User5', 'email5@email.com');
INSERT INTO public.requests (description, requester_id, created)
VALUES ('desc1', 1, DEFAULT),
       ('desc2', 2, DEFAULT),
       ('desc3', 3, DEFAULT);
INSERT INTO public.items (name, description, available, user_id, request_id)
VALUES ('item1', 'item1desc', true, 1, null),
       ('item2', 'item2desc', true, 1, null),
       ('item3', 'item3desc', false, 1, null),
       ('item4', 'item4desc', true, 2, 2),
       ('item5', 'item5desc', true, 3, 1);
INSERT INTO public.bookings (item_id, user_id, start_time, end_time, status)
VALUES (1, 2, '2024-08-16 00:00:00.000000', '2024-08-17 00:00:00.000000', 'APPROVED'),
       (1, 3, '2024-08-15 00:00:00.000000', '2024-08-15 23:00:00.000000', 'APPROVED'),
       (1, 4, '2024-08-18 00:00:00.000000', '2024-08-19 00:00:00.000000', 'APPROVED'),
       (1, 5, '2024-08-13 00:00:00.000000', '2024-08-14 00:00:00.000000', 'APPROVED'),
       (4, 3, '2024-08-16 00:00:00.000000', '2024-08-17 00:00:00.000000', 'APPROVED'),
       (4, 5, '2024-08-18 00:00:00.000000', '2024-08-19 00:00:00.000000', 'APPROVED'),
       (5, 2, '2024-08-15 00:00:00.000000', '2024-08-17 00:00:00.000000', 'APPROVED'),
       (1, 2, '2024-07-15 00:00:00.000000', '2024-07-17 00:00:00.000000', 'REJECTED'),
       (4, 2, '2024-07-15 00:00:00.000000', '2024-07-17 00:00:00.000000', 'CANCELLED'),
       (5, 2, '2024-07-15 00:00:00.000000', '2024-07-17 00:00:00.000000', 'WAITING');
INSERT INTO public.comments (text, item_id, user_id, created)
VALUES ('comment1', 1, 3, DEFAULT),
       ('comment2', 1, 5, DEFAULT),
       ('comment3', 3, 2, DEFAULT),
       ('comment4', 4, 3, DEFAULT);
