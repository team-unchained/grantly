import { z } from 'zod';

export const AppSchema = z.object({
  slug: z.string(),
  name: z.string().min(1, '앱 이름을 입력해주세요.'),
  imageUrl: z.string().optional(),
});

export type AppType = z.infer<typeof AppSchema>;
