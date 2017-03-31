declare class Vorpal {
    command(name:string, help:string): VorpalCommand;
}
declare class VorpalActionArguments {
    options: any;
}
declare class VorpalCommand {
    action(f:(args: VorpalActionArguments, callback: () => void ) => void):VorpalCommand;
    alias(alias:string): VorpalCommand;
    option(option:string):VorpalCommand;
}
declare function vorpal(): Vorpal;