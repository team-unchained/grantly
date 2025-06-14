import { SidebarTrigger } from '@grantly/components/ui/sidebar';
import {
  Breadcrumb,
  BreadcrumbItem as UIBreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@grantly/components/ui/breadcrumb';
import { Separator } from '@grantly/components/ui/separator';
import * as React from 'react';

export type BreadcrumbItemType = {
  title: string;
  url?: string;
};

interface HeaderProps {
  breadcrumbs?: BreadcrumbItemType[];
}

export const Header = ({ breadcrumbs = [] }: HeaderProps) => {
  return (
    <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12">
      <div className="flex items-center gap-2 px-4">
        <SidebarTrigger className="-ml-1" />
        <Separator orientation="vertical" className="mr-2 h-4" />
        <Breadcrumb>
          <BreadcrumbList>
            {breadcrumbs.map((breadcrumbItem, index) => (
              <React.Fragment key={breadcrumbItem.title}>
                <UIBreadcrumbItem className="hidden md:block">
                  {index === breadcrumbs.length - 1 || !breadcrumbItem.url ? (
                    <BreadcrumbPage>{breadcrumbItem.title}</BreadcrumbPage>
                  ) : (
                    <BreadcrumbLink href={breadcrumbItem.url}>
                      {breadcrumbItem.title}
                    </BreadcrumbLink>
                  )}
                </UIBreadcrumbItem>
                {index < breadcrumbs.length - 1 && (
                  <BreadcrumbSeparator className="hidden md:block" />
                )}
              </React.Fragment>
            ))}
          </BreadcrumbList>
        </Breadcrumb>
      </div>
    </header>
  );
};
