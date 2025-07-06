'use client';

import { Button } from '@grantly/components/ui/button';
import { useCallback, useState } from 'react';
import ReactCrop, { type Crop } from 'react-image-crop';
import { toast } from 'sonner';
import 'react-image-crop/dist/ReactCrop.css';

interface ImageCropModalProps {
  srcImage: string;
  onCropComplete: (croppedImageUrl: string) => void;
  onCancel: () => void;
}

export const ImageCropModal = ({
  srcImage,
  onCropComplete,
  onCancel,
}: ImageCropModalProps) => {
  const [crop, setCrop] = useState<Crop>({
    unit: 'px',
    width: 0,
    height: 0,
    x: 0,
    y: 0,
  });
  const [imageRef, setImageRef] = useState<HTMLImageElement | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);

  const onImageLoaded = useCallback((img: HTMLImageElement) => {
    setImageRef(img);
    const imgWidth = img.width;
    const imgHeight = img.height;

    // 정사각형 크기는 가로/세로 중 작은 쪽에 맞춤
    const cropSize = Math.min(imgWidth, imgHeight);

    // 중앙 위치 계산
    const cropX = (imgWidth - cropSize) / 2;
    const cropY = (imgHeight - cropSize) / 2;

    setCrop({
      unit: 'px',
      width: cropSize,
      height: cropSize,
      x: cropX,
      y: cropY,
    });
    return false;
  }, []);

  const handleCropComplete = async () => {
    if (!imageRef || !crop.width || !crop.height) return;

    setIsProcessing(true);

    try {
      const canvas = document.createElement('canvas');
      const scaleX = imageRef.naturalWidth / imageRef.width;
      const scaleY = imageRef.naturalHeight / imageRef.height;
      canvas.width = crop.width;
      canvas.height = crop.height;
      const ctx = canvas.getContext('2d');
      if (!ctx) return;

      ctx.drawImage(
        imageRef,
        (crop.x || 0) * scaleX,
        (crop.y || 0) * scaleY,
        crop.width * scaleX,
        crop.height * scaleY,
        0,
        0,
        crop.width,
        crop.height
      );

      const base64Image = canvas.toDataURL('image/jpeg');
      onCropComplete(base64Image);
    } catch (error) {
      toast.error('이미지 크롭 중 오류가 발생했습니다.');
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="bg-white p-8 rounded-xl shadow-xl min-w-[480px] min-h-[480px] max-w-[90vw] max-h-[90vh] flex flex-col items-center">
        <ReactCrop
          crop={crop}
          onChange={setCrop}
          aspect={1}
          minWidth={50}
          keepSelection
        >
          <img
            src={srcImage}
            alt="Crop"
            style={{ maxHeight: '60vh', maxWidth: '60vw', display: 'block' }}
            onLoad={(e) => onImageLoaded(e.currentTarget)}
          />
        </ReactCrop>
        <div className="flex gap-2 mt-4 justify-end">
          <Button variant="outline" onClick={onCancel} disabled={isProcessing}>
            취소
          </Button>
          <Button onClick={handleCropComplete} disabled={isProcessing}>
            {isProcessing ? '처리 중...' : '크롭 완료'}
          </Button>
        </div>
      </div>
    </div>
  );
};
