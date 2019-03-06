import {Component, ElementRef, ViewChild} from '@angular/core';
import { HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  pages: any;
  isLoading = true;
  @ViewChild('searchText') searchTerm: ElementRef;

  constructor(private _http: HttpClient) {}

   submit() {
      this._http.jsonp(`https://kgsearch.googleapis.com/v1/entities:search?query=`+this.searchTerm.nativeElement.value+`&key=AIzaSyBIhFWiwis9PnVuwmBcQ42t1uV7fDQs0P4&limit=1&indent=True` ,'callback')
      .subscribe((data: any) => {
        this.isLoading = false;
        this.pages = Object.keys(data.itemListElement).map(function (k) {
          // var i = data.query.pages[k];
          // return {title: i.title, body: i.extract, page: 'http://en.wikipedia.org/?curid=' + i.pageid}
              return {body:data.itemListElement[0].result.description }

        });
        console.log(this.pages);
      });
    }
}