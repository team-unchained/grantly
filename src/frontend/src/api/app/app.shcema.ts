import { z } from 'zod';

export const AppSchema = z.object({
  id: z.number().int(),
  slug: z.string(),
  name: z.string().min(1, '앱 이름을 입력해주세요.'),
  imageUrl: z.string(),
});

export type AppType = z.infer<typeof AppSchema>;
