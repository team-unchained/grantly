import { Path, UseFormRegister } from 'react-hook-form';
import { Input } from '@grantly/components/ui/input';
import { Label } from '@grantly/components/ui/label';

interface FormFieldProps<T extends Record<string, unknown>> {
  id: Path<T>;
  label: string;
  type?: string;
  error?: string;
  register: UseFormRegister<T>;
}
export const FormInputField = <T extends Record<string, unknown>>({
  id,
  label,
  type = 'text',
  error,
  register,
}: FormFieldProps<T>) => (
  <div className="grid gap-2">
    <Label htmlFor={String(id)}>{label}</Label>
    <Input id={String(id)} type={type} {...register(id)} />
    {error && <p className="body3 text-red-500">{error}</p>}
  </div>
);
