import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import User from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit{
    users: Array<User>;
    userDataTableSource: Array<User>;
    columndefs : Array<string> = ['first_name','last_name', 'email', 'actions'];


    constructor(private userService: UserService, private _snackBar: MatSnackBar, private _router: Router){
    }

    ngOnInit(){
        this.loadUsers();
    }

    loadUsers() {
        this.userService.getUsers().subscribe((val: Array<User>)=>{
            this.users = val;
            this.userDataTableSource = this.users;
        });
    }

    deleteUser(index) {
        let userID = this.userDataTableSource[index].user_id;
        this.userService.deleteUser(userID).subscribe(() => {
            this.loadUsers();
            this._snackBar.open("Deleted user successfully");
        });
    }

    editUser(index) {
        let userID = this.userDataTableSource[index].user_id;
        this._router.navigate([`/user/${userID}`]);
    }
}
