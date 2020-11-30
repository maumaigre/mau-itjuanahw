import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import User from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent implements OnInit{

    private userID: number;

    userForm = new FormGroup({
      first_name: new FormControl('', Validators.required),
      last_name: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
    });

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private userService: UserService, private _snackBar: MatSnackBar){
      this.activatedRoute.params.subscribe( params => {
        if (params.id && !isNaN(params.id)){
          this.loadUser(params.id);
        }
      })
    }

    ngOnInit(){

    }

    loadUser(userID) {
      this.userID = userID;
      this.userService.getUserbyID(userID).subscribe((user: User) => {
        this.userForm.setValue({
          first_name: user.first_name,
          last_name: user.last_name,
          email: user.email
        });
      });
    }

    onUserFormSubmit(){
      if (this.userID && this.userID !== null) {
        this.userService.patchUser(this.userID, this.userForm.value).subscribe(() => {
          this._snackBar.open('User updated successfully');
          this.router.navigateByUrl("/user-list");
        });
        return;
      }

      this.userService.addUser(this.userForm.value).subscribe(res=>{
        this._snackBar.open('User created successfully');
        this.router.navigateByUrl("/user-list");
      });
    }


    get first_name() { return this.userForm.get('first_name'); }

    get last_name() { return this.userForm.get('last_name'); }

    get email() { return this.userForm.get('email'); }
}
