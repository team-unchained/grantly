import { z } from 'zod';

export const UserSchema = z.object({
  id: z.number().int(),
  email: z.string().email(),
  name: z.string(),
});

export type UserType = z.infer<typeof UserSchema>;
