import { JavaVariable } from "./JavaVariable";

export class JavaMethod {
    constructor(
        readonly name: string,
        readonly params: JavaVariable[],
        readonly returnType: string,
        readonly modifiers: string[]
    ) {}
}