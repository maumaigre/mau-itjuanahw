import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CreateUserComponent } from './containers/create-user/create-user.component';
import { UserListComponent } from './containers/user-list/user-list.component';

const routes: Routes = [
  { path: 'create-user', component: CreateUserComponent },
  { path: 'user-list', component: UserListComponent },
  { path: 'user/:id', component: CreateUserComponent },
  { path: '**', redirectTo: 'create-user'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
